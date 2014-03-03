package com.hshacks.android.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hshacks.android.R;
import com.hshacks.android.time.TimeDelta;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends ArrayAdapter<ParseObject> {
    private Context mContext;
    private ArrayList<ParseObject> mAnnouncements;

    public NewsAdapter(Context context, ArrayList<ParseObject> announcements) {
        super(context, R.layout.announcement_cell);

        mContext = context;
        mAnnouncements = announcements;
    }

    public int getCount() {
        return mAnnouncements.size();
    }

    public ParseObject getItem(int position) {
        return mAnnouncements.get(position);
    }

    public long getItemId(int position) {
        return mAnnouncements.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View announcementCellView;
        if (convertView == null) {
            announcementCellView = inflater.inflate(R.layout.announcement_cell, null);
        } else {
            announcementCellView = (View) convertView;
        }

        String sender = mAnnouncements.get(position).getString("sender");
        if(sender == null || sender.isEmpty()) {
            sender = getContext().getString(R.string.default_announcement_sender);
        }
        TextView announcementSender = (TextView) announcementCellView.findViewById(R.id.announcement_sender);
        announcementSender.setText(sender);

        Date createdAt = mAnnouncements.get(position).getCreatedAt();
        String formattedTimeAgo = TimeDelta.formatTimeAgo(mContext, createdAt);
        TextView announcementTime = (TextView) announcementCellView.findViewById(R.id.announcement_time);
        announcementTime.setText(formattedTimeAgo);

        TextView announcementTitle = (TextView) announcementCellView.findViewById(R.id.announcement_title);
        announcementTitle.setText(mAnnouncements.get(position).getString("title"));

        TextView announcementBody = (TextView) announcementCellView.findViewById(R.id.announcement_body);
        announcementBody.setText(mAnnouncements.get(position).getString("body"));

        return announcementCellView;
    }

    //Disables selection
    public boolean isEnabled(int position) {
        return false;
    }
}