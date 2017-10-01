package com.henry.android.tourguideapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Henry on 2017/7/31.
 */


public class SpotAdapter extends ArrayAdapter<Spot> {


    public SpotAdapter(Activity context, ArrayList<Spot> spots) {
        super(context, 0, spots);
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
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get current spot by position
        Spot currentSpot = getItem(position);

        // Set Spot name to the TextView
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.location_name);

        nameTextView.setText(currentSpot.getSpotName());

        // Set Spot type to the TextView
        TextView typeTextView = (TextView) listItemView.findViewById(R.id.location_type);

        typeTextView.setText(currentSpot.getSpotType());

        // Find the background view
        View background = (View) listItemView.findViewById(R.id.background);

        // Check if an image is provided for this spot or not
        if (currentSpot.hasImage()) {
            // If an image is available, display the provided image based on the resource ID
            background.setBackgroundDrawable(getImage(parent.getContext(),currentSpot.getImageResource()));

            // Make sure the view is visible
            background.setVisibility(View.VISIBLE);
        }

        return listItemView;
    }

        /*
         * @param context 上下文
        * @param id 图片id
        * @return  bitmapDrawable 图片
             */
        public BitmapDrawable getImage(Context context, Integer id){
            BitmapFactory.Options opt =new BitmapFactory.Options();
            opt.inPreferredConfig= Bitmap.Config.RGB_565;
            opt.inPurgeable =true;
            opt.inInputShareable =true;
            InputStream is  =context.getResources().openRawResource(id);
            Bitmap bm =BitmapFactory.decodeStream(is,null,opt);
            BitmapDrawable bd = new BitmapDrawable(context.getResources(),bm);
            return bd;
        }
}