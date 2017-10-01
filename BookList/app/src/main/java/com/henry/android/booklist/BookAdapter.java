package com.henry.android.booklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Henry on 2017/8/24.
 */

public class BookAdapter extends ArrayAdapter{
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context,0, books);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        final Book book = (Book) getItem(position);

        String bookName = book.getBookName();

        JSONArray authors = book.getAuthor();

        String author = splitAuthorsArray(authors);

        String publisher = book.getPublisher();

        TextView bookNameView = (TextView) listItemView.findViewById(R.id.book_name);
        bookNameView.setText(bookName);

        TextView authorNameView = (TextView) listItemView.findViewById(R.id.author_name);
        authorNameView.setText(author);

        TextView publisherNameView = (TextView) listItemView.findViewById(R.id.publisher_name);
        publisherNameView.setText(publisher);

        return listItemView;
    }

    private String splitAuthorsArray(JSONArray authors) {
        String author = "";

        if(authors != null) {
            for (int i = 0; i < authors.length(); i++) {
                author = author + authors.optString(i);
            }
            return author;
        }
        else {
            return author;
        }
    }
}
