package com.mydemoapp2;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class VersionAdapter<V> extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Connection> mDataSource;


    public VersionAdapter(Context context, ArrayList<Connection> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Connection conn){
        mDataSource.add(conn);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = mInflater.inflate(R.layout.item_layout_user, parent, false);


        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.version_list_title);


        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.version_name);


        Connection recipe = (Connection) getItem(position);

        titleTextView.setText(recipe.getConnectionUrl());
        subtitleTextView.setText(recipe.getName());

        return rowView;
    }
}
