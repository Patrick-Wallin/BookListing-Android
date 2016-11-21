package com.patrickwallin.android.booklisting;

import java.util.ArrayList;

/**
 * Created by Patrick on 11/19/2016.
 */
public class BookListingInformation {
    private String mTitle;
    private ArrayList<String> mAuthor;
    private String mDate;

    public BookListingInformation(String title, ArrayList<String> author, String date) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
    }

    public void setTitle(String title) { mTitle = title; }
    public String getTitle() { return mTitle; }

    public void setAuthor(ArrayList<String> author) { mAuthor = author; }
    public ArrayList<String> getAuthor() { return mAuthor; }

    public void setDate(String date) { mDate = date; }
    public String getDate() { return mDate; }
}
