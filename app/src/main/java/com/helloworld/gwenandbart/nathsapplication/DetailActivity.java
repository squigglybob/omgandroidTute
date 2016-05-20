package com.helloworld.gwenandbart.nathsapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.squareup.picasso.Picasso;

/**
 * Created by gwenandbart on 20/05/16.
 */
public class DetailActivity extends Activity {

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/"; //13
    String mImageURL; // 13
    ShareActionProvider mShareActionProvider; //14

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tell the activity which XML layout to load
        setContentView(R.layout.activity_detail);

        // Enable the Up button for more navigation options
//        getActionBar().setDisplayHomeAsUpEnabled(true); // this doesn't seem to be necessary

        // Access the imageview from XML file
        ImageView imageview = (ImageView) findViewById(R.id.img_cover);

//        13. Unpack  the coverID from it's trip inside your Intent
        String coverID = this.getIntent().getExtras().getString("coverID");

        // See if there is a valid coverID
        if (coverID.length() > 0) {

            // Use the ID to construct an image URL
            mImageURL = IMAGE_URL_BASE + coverID + "-L.jpg";

            // Use Picasso to load the image
            Picasso.with(this).load(mImageURL).placeholder(R.drawable.img_books_loading).into(imageview);
        }
    }

    private void setShareIntent() {

        // Create an intent with the contents of the textview
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Recommendation!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mImageURL);

        // Make sure the provider knows it should work with that Intent
        mShareActionProvider.setShareIntent(shareIntent);
    }

}





































