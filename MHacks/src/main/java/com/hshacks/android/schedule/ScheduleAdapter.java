package com.hshacks.android.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hshacks.android.R;
import com.hshacks.android.time.TimeDelta;
import com.hshacks.android.views.PinnedSectionListView;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScheduleAdapter extends ArrayAdapter<ScheduleAdapter.Item> implements PinnedSectionListView.PinnedSectionListAdapter{
    private Context mContext;
    private ArrayList<Item> mItems;
    private ArrayList<ParseObject> mEvents;

    public ScheduleAdapter(Context context, ArrayList<ParseObject> announcements) {
        super(context, R.layout.event_cell);

        mContext = context;
        mEvents = announcements;

        mItems = new ArrayList();
        updateViews();
    }

    public void updateViews() {
        mItems.clear();

        Item currentSection = null;

        if (!mEvents.isEmpty()) {
            currentSection = new Item(mEvents.get(0).getDate("time"));
            mItems.add(currentSection);
        }

        for (ParseObject event : mEvents) {
            Item eventItem = new Item(event);

            if (!formatDate(currentSection.time).equals(formatDate(eventItem.time))) {
                currentSection = new Item(event.getDate("time"));
                mItems.add(currentSection);
            }

            mItems.add(eventItem);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        updateViews();
        super.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).hashCode();
    }

    public String formatDate(Date date) {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        dateFormat.applyPattern("EEEE, MMMM d");
        return dateFormat.format(date);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Item item = mItems.get(position);

        View cellView;
        if (item.type == Item.ITEM) {
            cellView = inflater.inflate(R.layout.event_cell, null);
        } else {
            cellView = inflater.inflate(R.layout.section_header, null);
        }

        if (item.type == Item.ITEM && item.content != null) {
            ParseObject event = item.content;

            TextView eventName = (TextView) cellView.findViewById(R.id.event_name);
            eventName.setText(event.getString("name"));

            TextView eventTime = (TextView) cellView.findViewById(R.id.event_time);
            SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
            dateFormat.applyPattern("h:mm aaa");
            Date offsetDate = TimeDelta.offsetDateFromUTCToEST(event.getDate("time"));
            eventTime.setText(dateFormat.format(offsetDate));

            TextView eventDescription = (TextView) cellView.findViewById(R.id.event_description);
            String eventDescriptionText = event.getString("description");
            if(eventDescriptionText == null || eventDescriptionText.isEmpty()) {
                eventDescription.setVisibility(View.GONE);
            } else {
                eventDescription.setText(event.getString("description"));
            }
        }
        else {
            TextView header = (TextView) cellView.findViewById(R.id.section_header);
            TextView subHeader = (TextView) cellView.findViewById(R.id.section_subheader);

            header.setText(formatDate(item.time));
            subHeader.setText("");
        }

        return cellView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

    //Disables selection
    public boolean isEnabled(int position) {
        return false;
    }

    static class Item {
        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final ParseObject content;
        public final Date time;

        public Item(ParseObject content) {
            this.type = ITEM;
            this.content = content;
            this.time = TimeDelta.offsetDateFromUTCToEST(content.getDate("time"));
        }

        public Item(Date time) {
            this.time = TimeDelta.offsetDateFromUTCToEST(time);
            this.type = SECTION;
            this.content = null;
        }

    }

}