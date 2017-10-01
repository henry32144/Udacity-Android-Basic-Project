package com.henry.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.henry.android.inventoryapp.data.ProductContract;

/**
 * Created by Henry on 2017/9/14.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.item_product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_product_price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.item_product_quantity);
        final TextView soldTextView = (TextView) view.findViewById(R.id.item_product_sold_number);
        Button buttonView = (Button) view.findViewById(R.id.item_button);

        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry._ID));
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
        String productPrice =  "$" + cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        String productQuantity = Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)));
        String soldNumber = Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_SOLD)));

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
        soldTextView.setText(soldNumber);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantityTextView.getText() + "");
                int newQuantity = soldOne(currentQuantity, context);
                if(currentQuantity > 0) {
                    Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
                    int sold = Integer.parseInt(soldTextView.getText() + "") + 1;
                    saveQuantity(currentProductUri, newQuantity , sold , context);
                    quantityTextView.setText(String.valueOf(newQuantity));
                }
            }
        });
    }

    private void saveQuantity(Uri uri, int quantity , int soldNumber ,Context context) {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SOLD, soldNumber);
        int rowsAffected = context.getContentResolver().update(uri, values, null, null);
    }

    private int soldOne(int current, Context context) {
        if(current > 0) {
            return current - 1;
        }
        else {
            Toast.makeText(context, context.getString(R.string.item_sold_out),
                    Toast.LENGTH_SHORT).show();
            return 0;
        }
    }
}
