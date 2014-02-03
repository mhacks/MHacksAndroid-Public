package com.mhacks.android.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mhacks.android.R;

import java.util.List;

/**
 * Created by damian on 1/12/14.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {
    private Context mContext;
    private List<NavigationDrawerItem> mItems;
    private int mCurrent;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> items) {
        super(context, R.layout.award_cell);

        mContext = context;
        mItems = items;
        mCurrent = -1;
    }

    public int getCount() {
        return mItems.size();
    }

    public NavigationDrawerItem getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return mItems.get(position).hashCode();
    }

    public void setCurrent(int position) {
        mCurrent = position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View navItemView;
        if (convertView == null) {
            navItemView = inflater.inflate(R.layout.navigation_item, null);
        } else {
            navItemView = convertView;
        }

        NavigationDrawerItem item = mItems.get(position);

        ImageView icon = (ImageView) navItemView.findViewById(R.id.navigation_item_icon);
        icon.setImageResource(item.iconID);

        TextView text = (TextView) navItemView.findViewById(R.id.navigation_item_title);
        text.setText(item.title);

        if (position == mCurrent) {
            navItemView.setBackgroundResource(R.color.custom_theme_color);
        } else {
            navItemView.setBackgroundResource(android.R.color.transparent);
        }

        return navItemView;
    }

}