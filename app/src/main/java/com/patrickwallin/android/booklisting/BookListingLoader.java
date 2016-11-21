package com.patrickwallin.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Patrick on 11/19/2016.
 */
public class BookListingLoader  extends AsyncTaskLoader<List<BookListingInformation>> {
    private String LOG_TAG = BookListingLoader.class.getName();

    /** Query URL */
    private String mUrl;

    public BookListingLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public List<BookListingInformation> loadInBackground() {
        if (mUrl == null || mUrl.isEmpty()) {
            return null;
        }
        List<BookListingInformation> books = QueryUtils.fetchBookListingData(mUrl);

        return books;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
