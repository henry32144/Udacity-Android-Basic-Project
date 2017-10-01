package com.henry.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Henry on 2017/8/28.
 */

public class NewsAdapter extends ArrayAdapter {

    private static int DATE_STRING_START = 0;
    private static int DATE_STRING_END = 10;

    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            holder.newsTitleText = (TextView) convertView.findViewById(R.id.list_item_news_title);
            holder.newsTagText = (TextView) convertView.findViewById(R.id.list_item_news_tag);
            holder.newsDateText = (TextView) convertView.findViewById(R.id.list_item_time_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final News news = (News) getItem(position);

        String newsTitle = news.getNewsTitle();

        String newsTag = news.getNewsTag();

        String newsDate = splitDateString(news.getNewsDate());

        holder.newsTitleText.setText(newsTitle);

        holder.newsTagText.setText(newsTag);

        holder.newsDateText.setText(newsDate);

        return convertView;
    }

    private String splitDateString(String date) {
        //EX: 2017-08-29
        String newDate = date.substring(DATE_STRING_START, DATE_STRING_END);
        return newDate;
    }
}
