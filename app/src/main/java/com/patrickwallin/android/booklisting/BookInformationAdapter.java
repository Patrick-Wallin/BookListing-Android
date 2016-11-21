package com.patrickwallin.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 11/19/2016.
 */
public class BookInformationAdapter  extends ArrayAdapter<BookListingInformation> {
    public BookInformationAdapter(Context context, List<BookListingInformation> objects) {
        super(context, 0, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_listing_item, parent, false);
        }

        BookListingInformation info = getItem(position);

        TextView tvTitle = (TextView) listItemView.findViewById(R.id.title_text_view);
        TextView tvAuthor = (TextView) listItemView.findViewById(R.id.author_text_view);
        TextView tvDate = (TextView) listItemView.findViewById(R.id.date_text_view);

        tvTitle.setText(info.getTitle());
        tvDate.setText(info.getDate());

        StringBuilder sbAuthors = new StringBuilder();
        ArrayList<String> authors = info.getAuthor();
        if(authors != null && !authors.isEmpty()) {
            for(int i = 0; i < authors.size(); i++) {
                if(!sbAuthors.toString().isEmpty()) sbAuthors.append("\n");
                sbAuthors.append(authors.get(i));
            }
        }
        tvAuthor.setText(sbAuthors.toString());

        return listItemView;
    }
}

