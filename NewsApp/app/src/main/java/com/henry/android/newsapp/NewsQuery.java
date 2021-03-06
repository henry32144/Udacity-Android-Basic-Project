package com.henry.android.newsapp;

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
 * Created by Henry on 2017/8/28.
 */

public final class NewsQuery {

    private static final String LOG_TAG = "NewsQuery";

    public NewsQuery() {
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
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
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

    private static List<News> extractFeatureFromJson(String newsJSON) {
        // if json is empty, return null
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // create empty news array list
        List<News> newsList = new ArrayList<>();


        try {

            // create json object
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject response = baseJsonResponse.getJSONObject(Constant.JSONKey.response);

            JSONArray newsArray = response.getJSONArray(Constant.JSONKey.results);

            for (int i = 0; i < newsArray.length(); i++) {

                //get json data from array
                JSONObject currentNew = newsArray.getJSONObject(i);

                // Get news title if it has
                String newsTitle = currentNew.optString(Constant.JSONKey.webTitle);

                // Get news tag if it has
                String newsTag = currentNew.optString(Constant.JSONKey.sectionName);

                // Get news date if it has
                String newsDate = currentNew.optString(Constant.JSONKey.webPublicationDate);

                // Get news url page if it has
                String url = currentNew.optString(Constant.JSONKey.webUrl);

                // create new news object
                News newsObj = new News(newsTitle, newsTag, newsDate, url);

                newsList.add(newsObj);
            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        return newsList;
    }

    public static List<News> fetchNewsData(String requestUrl) {

        //create URL object
        URL url = createURL(requestUrl);

        // send HTTP request to url and parse the data
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<News> newsList = extractFeatureFromJson(jsonResponse);

        return newsList;
    }
}
