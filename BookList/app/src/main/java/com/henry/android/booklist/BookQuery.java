package com.henry.android.booklist;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henry on 2017/8/24.
 */

public final class BookQuery {

    private static final String LOG_TAG = "BookQuery";

    public BookQuery() {
    }

    private static URL createURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // return if url is null
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // if request success, continue parse data
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //Deal with input stream
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // if json is empty, return null
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // create empty book array list
        List<Book> books = new ArrayList<>();


        try {

            // create json object
            JSONObject baseJsonResponse = new JSONObject(bookJSON);


            JSONArray bookArray = baseJsonResponse.getJSONArray(Constant.JSONKey.items);


            for (int i = 0; i < bookArray.length(); i++) {

                //get json data from array
                JSONObject currentBook = bookArray.getJSONObject(i);


                JSONObject volumeInfo = currentBook.getJSONObject(Constant.JSONKey.volumnInfo);

                // Get book title if it has
                String bookName = volumeInfo.optString(Constant.JSONKey.title);

                // Get book author if it has
                JSONArray author = volumeInfo.optJSONArray(Constant.JSONKey.authors);

                // Get book publisher if it has
                String publisher = volumeInfo.optString(Constant.JSONKey.publisher);

                // Get book link page if it has
                String url = volumeInfo.optString(Constant.JSONKey.infoLink);


                // create new book object
                Book book = new Book(bookName, author, publisher, url);

                // add book to books
                books.add(book);
            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }


        return books;
    }

    public static List<Book> fetchBookData(String requestUrl) {


        URL url = createURL(requestUrl);

        // Execute HTTP request and receive JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> books = extractFeatureFromJson(jsonResponse);


        return books;
    }
}
