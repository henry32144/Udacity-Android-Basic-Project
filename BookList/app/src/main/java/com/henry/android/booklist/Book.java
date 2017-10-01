package com.henry.android.booklist;

import org.json.JSONArray;

/**
 * Created by Henry on 2017/8/24.
 */

public class Book {
    private String bookName;
    private JSONArray author;
    private String publisher;
    private String url;

    public Book(String bookName, JSONArray author, String publisher, String url) {
        this.bookName = bookName;
        this.author = author;
        this.publisher = publisher;
        this.url = url;
    }

    public String getBookName() {
        return bookName;
    }

    public JSONArray getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getUrl() {
        return url;
    }
}
