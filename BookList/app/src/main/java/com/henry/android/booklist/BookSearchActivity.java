package com.henry.android.booklist;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookSearchActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    private static String GOOGLE_BOOKAPI_URL = "";

    private BookAdapter mAdapter;

    private static final int BOOK_LOADER_ID = 1;

    private TextView mEmptyStateTextView;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        //Get data from extras
        Bundle extras = intent.getExtras();

        GOOGLE_BOOKAPI_URL = extras.getString("url");

        extras.clear();

        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        progressBar = (ProgressBar) findViewById(R.id.loading_indicator);

        bookListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        bookListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);

            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get current book by position
                Book currentBook = (Book) mAdapter.getItem(position);

                // if current book has url
                if(currentBook.getUrl() != null) {
                    Uri bookUri = Uri.parse(currentBook.getUrl());

                    // create intent
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                    // send intent
                    startActivity(websiteIntent);
                }
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // 为给定 URL 创建新 loader

        return new BookLoader(this, GOOGLE_BOOKAPI_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        mAdapter.clear();


        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

        progressBar.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_book);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // 重置 Loader，以便能够清除现有数据。
        mAdapter.clear();
    }
}
