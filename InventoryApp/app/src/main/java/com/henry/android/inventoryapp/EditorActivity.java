package com.henry.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.henry.android.inventoryapp.data.ProductContract;

import java.io.IOException;
import java.io.InputStream;

import static android.widget.Toast.makeText;
/**
 * Created by Henry on 2017/9/13.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //these variable is to check if value is changed or not
    private String oriNameString;
    private String oriPriceValue;
    private String oriQuantityValue;
    private String oriDistributorString;
    private String oriSoldValue;

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mDistributorEditText;

    private EditText mSoldNumberEditText;

    private Uri mCurrentProductUri;

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private static final int PICK_IMAGE = 100;

    private ImageView mImageView;

    private boolean imgChanged = false;

    private String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        imgChanged = false;
        imageString = "";

        Intent intent = getIntent();
        mCurrentProductUri  = intent.getData();

        if(mCurrentProductUri  == null) {
            this.setTitle(getString(R.string.editor_label_new));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            this.setTitle(getString(R.string.editor_label_edit));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.editor_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveProduct()) {
                    finish();
                }
                else {
                    Toast.makeText(EditorActivity.this, R.string.editor_product_name_empty,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mDistributorEditText = (EditText) findViewById(R.id.editor_distributor);
        mSoldNumberEditText = (EditText) findViewById(R.id.edit_product_sold);
        Button mMinusButton = (Button) findViewById(R.id.editor_minus_button);
        Button mPlusButton = (Button) findViewById(R.id.editor_plus_button);
        Button mImgButton = (Button) findViewById(R.id.editor_img_button);
        mImageView = (ImageView) findViewById(R.id.editor_product_img);

        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusEvent();
            }
        });

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusEvent();
            }
        });

        mImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        FloatingActionButton order = (FloatingActionButton) findViewById(R.id.editor_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameString = mNameEditText.getText().toString().trim();
                String distributorString = mDistributorEditText.getText().toString().trim();
                if(!orderProduct(distributorString,nameString)) {
                    Toast.makeText(EditorActivity.this, R.string.editor_order_something_empty,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete_product:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                // Respond to a click on the "Up" arrow button in the app bar
                return true;
            case android.R.id.home:
                if (!checkHasChanged()) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceValue = mPriceEditText.getText().toString().trim();
        String quantityValue = mQuantityEditText.getText().toString().trim();
        String distributorString = mDistributorEditText.getText().toString().trim();
        String soldValue = mSoldNumberEditText.getText().toString().trim();


        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceValue) &&
                TextUtils.isEmpty(quantityValue) && TextUtils.isEmpty(distributorString) &&TextUtils.isEmpty(soldValue)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        }

        //Product name cant be empty
        if(TextUtils.isEmpty(nameString)) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);

        // if distributorString is empty, replace with None
        if(TextUtils.isEmpty(distributorString)) {
            distributorString = getString(R.string.editor_distributor_replacer);
        }
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_DISTRIBUTOR, distributorString);

        // If the price or quantity or sold number are  not valid, don't try to parse the string into an
        // integer value. Use 0 by default.
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SOLD, checkValidValue(soldValue));
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, checkValidValue(priceValue));
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, checkValidValue(quantityValue));
        if(imgChanged) {
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMG, imageString);
        }

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
                return true;
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
                return true;
            } else {
                // Otherwise, the update was successful and we can display a toast.
                makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }

    private int checkValidValue(String value) {
        int newValue = 0;
        if (!TextUtils.isEmpty(value) && Integer.parseInt(value) > 0) {
            newValue = Integer.parseInt(value);
        }
        return newValue;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SOLD,
                ProductContract.ProductEntry.COLUMN_PRODUCT_DISTRIBUTOR,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMG};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int soldColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SOLD);
            int distributorColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_DISTRIBUTOR);
            int imgColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMG);
            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            int soldNum = data.getInt(soldColumnIndex);
            String distributor = data.getString(distributorColumnIndex);
            imageString = data.getString(imgColumnIndex);

            //Save origin data from database
            oriNameString = name;
            oriPriceValue = String.valueOf(price);
            oriQuantityValue = String.valueOf(quantity);
            oriSoldValue = String.valueOf(soldNum);
            oriDistributorString = distributor;

            // Update the views on the screen with the values from the database
            mNameEditText.setText(oriNameString);
            mPriceEditText.setText(oriPriceValue);
            mQuantityEditText.setText(oriQuantityValue);
            mSoldNumberEditText.setText(oriSoldValue);
            mDistributorEditText.setText(oriDistributorString);

            if(imageString != null && imageString.length() > 0) {

                Uri imgUri = Uri.parse(imageString);
                //ask for read external storage permission
                try {
                    InputStream ims = getContentResolver().openInputStream(imgUri);
                    // just display image in imageview
                    Bitmap bitMapImg = getResizedBitmap(BitmapFactory.decodeStream(ims)
                            ,(int) getResources().getDimension(R.dimen.editor_image_width)
                            ,(int) getResources().getDimension(R.dimen.editor_image_height)
                            );
                    mImageView.setImageBitmap(bitMapImg);
                    if(ims != null) {
                        ims.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSoldNumberEditText.setText("");
        mDistributorEditText.setText("");
        mImageView.setImageResource(android.R.color.transparent);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkHasChanged() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceValue = mPriceEditText.getText().toString().trim();
        String quantityValue = mQuantityEditText.getText().toString().trim();
        String distributorString = mDistributorEditText.getText().toString().trim();
        String soldValue = mSoldNumberEditText.getText().toString().trim();

        if(!nameString.equals(oriNameString) || !priceValue.equals(oriPriceValue)
                || !quantityValue.equals(oriQuantityValue)
                || !distributorString.equals(oriDistributorString)
                || !soldValue.equals(oriSoldValue)) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!checkHasChanged()) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_product);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProdcut();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the prodcut.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProdcut() {
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();
        }
    }

    //Handle minus quantity event
    private void minusEvent() {
        int newValue;
        String quantityValue = mQuantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(quantityValue) && Integer.parseInt(quantityValue) > 0) {
            newValue = Integer.parseInt(quantityValue) - 1;
            mQuantityEditText.setText(String.valueOf(newValue));
        }
        else if (!TextUtils.isEmpty(quantityValue) && Integer.parseInt(quantityValue) <= 0) {
            Toast.makeText(EditorActivity.this, R.string.edit_quantity_zero_text,
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Handle plus quantity event
    private void plusEvent() {
        int newValue;
        String quantityValue = mQuantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(quantityValue)) {
            newValue = Integer.parseInt(quantityValue) + 1;
            mQuantityEditText.setText(String.valueOf(newValue));
        }
    }

    private boolean orderProduct(String address, String productName) {
        if(TextUtils.isEmpty(address) || TextUtils.isEmpty(productName)) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {address});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_summary_email_subject , productName));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        return true;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            imgChanged = true;
            imageString = imageUri.toString();
            mImageView.setImageURI(imageUri);
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create martix for sclae
        Matrix matrix = new Matrix();
        // resize the bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        //create new bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}