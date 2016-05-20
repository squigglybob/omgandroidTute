package com.helloworld.gwenandbart.nathsapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;

    ListView mainListView;
    JSONAdapter mJSONAdapter;
    ArrayList mNameList = new ArrayList();

    ShareActionProvider mShareActionProvider;

    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences mSharedPreferences;

    private static final String     QUERY_URL = "http://openlibrary.org/search.json?q=";

    ProgressDialog  progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //      11. Add a spinning progress bar ( and make sure it's off)
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        setProgressBarIndeterminateVisibility(false);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//      1. Access the textview defined in layout xml and then set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);

//      2. Access the button defined and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

//      3. Access the EditText defined in layout xml
        mainEditText = (EditText) findViewById(R.id.main_edittext);

//      4. Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

//     5. Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

//      7. Greet the user, or ask for their name if new
        displayWelcome();

//      10. Create a JSONAdapter for the ListView
        mJSONAdapter = new JSONAdapter(this, getLayoutInflater());

//        Set the ListView to use the Array Adapter
        mainListView.setAdapter(mJSONAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu.
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }

        // Create an Intent to share your content
        setShareIntent();

        return true;
    }

    private void setShareIntent() {

        if (mShareActionProvider != null) {

            // create an Intent with the contents of the TextView
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

            // Make sure the provider knows
            // it should work with that Intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onClick(View v) {

        // Test the button
        mainTextView.setText(mainEditText.getText().toString()
                + " is learning Android development!");

//        Also add that value to the list shown in the ListView
        mNameList.add(mainEditText.getText().toString());
        mJSONAdapter.notifyDataSetChanged();

//        6. the text you'd like to share has changed and you need to update
        setShareIntent();

//        9. Take what was typed into the EditText and use in search
        queryBooks(mainEditText.getText().toString());
        progress = new ProgressDialog(v.getContext());
        progress.setMessage("Downloading Book Data :O)");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        //progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 12. Now that the user's chosen a book, grab the cover data
        JSONObject jsonObject = (JSONObject) mJSONAdapter.getItem(position);
        String coverID = jsonObject.optString("cover_i","");

        // create an Intent to take you over to a new DetailActivity
        Intent detailIntent = new Intent(this, DetailActivity.class);

        // pack away the data about the cover into your Intent before you head out
        detailIntent.putExtra("coverID", coverID);

        // TODO: add any other data you'd like as Extras
        Log.d("omg android", jsonObject.toString());
        // Stat the next activity using your prepared Intent
        startActivity(detailIntent);

    }

    public void displayWelcome() {

        // Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Read the user's name
        // or an empty string if nothing found
        String name = mSharedPreferences.getString(PREF_NAME,"");

        if (name.length() > 0) {

            // If the name is valid, display a Toast welcoming them
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        } else {
            // Otherwise show a dialog to ask for their name
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            // Create EditText viwe for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            // Make an "OK" button to save the name
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Grab the EditText's input
                    String inputName = input.getText().toString();
                    String inputNameCap = inputName.substring(0,1).toUpperCase() + inputName.substring(1);

                    // Put it into memory (don't forget to commit!)
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME,inputNameCap);
                    e.commit();

                    // Welcome the new user
                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                }
            });

            // Make a Cancel button that simply dismisses the alert
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            alert.show();
        }
    }

    private void queryBooks(String searchString) {

//      Prepare string for use in url, as it might have reserved characters
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
//            if this fails for some reason, let the user know why
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

//        Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("omg android", QUERY_URL + urlString);

//        Have the client get  a JSON array of data
//        and define how to respond
        client.get(QUERY_URL + urlString,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
//                        Display a toast on success
                        Toast.makeText(getApplicationContext(),  "Success!", Toast.LENGTH_LONG).show();

                        // Log the results also

//                        update the data in your custom method.
                        mJSONAdapter.updateData(jsonObject.optJSONArray("docs"));

//                        11. stop progress bar
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                        // Display a Toast message to announce the failure
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                        // Log error message to help solve any problems
                        Log.e("omg android", statusCode + " " + throwable.getMessage());
//                        stop progress bar
                        progress.dismiss();
                    }
                });
    }
}





































