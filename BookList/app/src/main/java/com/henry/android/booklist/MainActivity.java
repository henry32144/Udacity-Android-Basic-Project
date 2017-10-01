package com.henry.android.booklist;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public String GOOGLE_BOOKAPI_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    public String LIMIT_RESULT = "&maxResults=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        //Find button view
        Button searchButton = (Button) findViewById(R.id.intro_search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchBox = (EditText) findViewById(R.id.intro_search_box);

                String searchText = formatSearchString(searchBox.getText().toString());

                String urlString = null;
                try {
                    urlString = GOOGLE_BOOKAPI_URL + URLEncoder.encode(searchText, "UTF-8") + LIMIT_RESULT;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Intent urlIntent = new Intent(MainActivity.this, BookSearchActivity.class);
                //Create extras to pass data
                Bundle extras = new Bundle();

                extras.clear();

                extras.putString("url", urlString);

                urlIntent.putExtras(extras);

                startActivity(urlIntent);
            }
        });
    }

    private String formatSearchString(String searchText) {

        String formatedString = searchText.trim();

        return formatedString;
    }
}