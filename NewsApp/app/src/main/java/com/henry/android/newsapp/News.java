package com.henry.android.newsapp;

/**
 * Created by Henry on 2017/8/28.
 */

public class News {
    private String newsTitle;
    private String newsTag;
    private String newsDate;
    private String newsURL;

    public News(String title, String tag, String date, String url) {
        this.newsTitle = title;
        this.newsTag = tag;
        this.newsDate = date;
        this.newsURL = url;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsTag() {
        return newsTag;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public String getNewsURL() {
        return newsURL;
    }
}
