package com.patrickwallin.android.booklisting;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookListingInformation>> {
    private static final int BOOK_LISTING_LOADER_ID = 1;

    public static final String LOG_TAG = MainActivity.class.getName();

    private static final String GOOGLE_BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=40&q=";
    private String searchURL = "";

    private BookInformationAdapter mAdapter;

    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("OnCreate","oncreate");
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_text_view);
        mEmptyStateTextView.setText(R.string.enter_search_input);

        mAdapter = new BookInformationAdapter(this, new ArrayList<BookListingInformation>());

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setEmptyView(mEmptyStateTextView);
        listView.setAdapter(mAdapter);

        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        new BookListingAsyncTask().execute(searchURL);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LISTING_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        Log.v("OnCreateOptionsMenu","oncreateoptionsmenu");
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v("rotate?",query);
            if(!query.trim().isEmpty()) {
                searchURL = GOOGLE_BOOK_REQUEST_URL + query.trim();
                Log.v("search url",searchURL);

                View loadingIndicator = findViewById(R.id.loading_spinner);
                loadingIndicator.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText("");

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new BookListingAsyncTask().execute(searchURL);

                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(BOOK_LISTING_LOADER_ID, null, this);

                }else {
                    loadingIndicator.setVisibility(View.GONE);

                    mEmptyStateTextView.setText(R.string.no_connection);
                }

            }else {
                mEmptyStateTextView.setText(R.string.enter_search_input);
            }

        }
    }

    @Override
    public Loader<List<BookListingInformation>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG,"OnCreateLoader");
        return new BookListingLoader(this, searchURL);

    }

    @Override
    public void onLoadFinished(Loader<List<BookListingInformation>> loader, List<BookListingInformation> data) {
        Log.v(LOG_TAG,"OnLoadFinished");
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        if(data == null) {
            return;
        }

        mEmptyStateTextView.setText(R.string.no_book);

        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BookListingInformation>> loader) {
        Log.v(LOG_TAG,"OnLoaderReset");
        mAdapter.clear();
    }

    private class BookListingAsyncTask extends AsyncTask<String,Void,List<BookListingInformation>> {

        @Override
        protected List<BookListingInformation> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null || urls[0].trim().isEmpty() ) {
                return null;
            }
            List<BookListingInformation> books = QueryUtils.fetchBookListingData(urls[0]);

            return books;
        }

        @Override
        protected void onPostExecute(final List<BookListingInformation> data) {
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            if(data == null) {
                return;
            }

            mAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }

        }
    }



}
