package com.mhacks.android.concierge;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mhacks.android.R;
import com.mhacks.android.views.PinnedSectionListView;
import com.parse.ParseObject;

import java.util.ArrayList;

public class ConciergeAdapter extends ArrayAdapter<ConciergeAdapter.Item> implements PinnedSectionListView.PinnedSectionListAdapter {
    private Context mContext;
    private ArrayList<ParseObject> mMentors;
    private ArrayList<Item> mItems;

    public ConciergeAdapter(Context context, ArrayList<ParseObject> mentors) {
        super(context, R.layout.event_cell);

        mContext = context;
        mMentors = mentors;

        mItems = new ArrayList();
        updateViews();
    }

    @Override
    public void notifyDataSetChanged() {
        updateViews();
        super.notifyDataSetChanged();
    }

    public void updateViews() {
        Item section = null;
        mItems.clear();
        if (!mMentors.isEmpty()) {
            section = new Item(getCompany(mMentors.get(0)));
            mItems.add(section);
        }
        for (ParseObject mentor : mMentors) {
            String company = getCompany(mentor);
            if (company != null && !company.equals(section.header)) {
                section = new Item(company);
                mItems.add(section);
            }
            mItems.add(new Item(mentor));
        }
    }


    public String getCompany(ParseObject mentor) {
        String result = mentor.getString("company");
        if (result == null || result.equals("")) return mContext.getString(R.string.mhacks_staff);
        else return result;
    }

    public int getCount() {
        return mItems.size();
    }

    public Item getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return mItems.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Item item = mItems.get(position);

        View cellView;
        if (item.type == Item.ITEM) cellView = inflater.inflate(R.layout.concierge_cell, null);
        else cellView = inflater.inflate(R.layout.section_header, null);


        if (item.type == Item.ITEM && item.content != null) {
            final ParseObject event = item.content;

            TextView conciergeName = (TextView) cellView.findViewById(R.id.concierge_name);
            conciergeName.setText(event.getString("name"));

            TextView eventDescription = (TextView) cellView.findViewById(R.id.concierge_skills);
            eventDescription.setText(event.getString("skills"));

            ImageView imageView = (ImageView) cellView.findViewById(R.id.concierge_logo);
            if (event.getString("contactType").equals("twitter")) {
                imageView.setImageResource(R.drawable.ic_twitter);
            } else if (event.getString("contactType").equals("email")) {
                imageView.setImageResource(R.drawable.ic_email);
            } else if (event.getString("contactType").equals("phone")) {
                imageView.setImageResource(R.drawable.ic_phone);
            }

            cellView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(event.getString("contactType").equals("twitter")) {
                        String tweetUrl = "https://twitter.com/intent/tweet?text="
                                +String.format(mContext.getString(R.string.concierge_tweet),
                                event.getString("contactInfo"));
                        Uri uri = Uri.parse(tweetUrl);
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                    else if(event.getString("contactType").equals("email")) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{event.getString("contactInfo")});
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                mContext.getString(R.string.concierge_email_title));
                        intent.putExtra(Intent.EXTRA_TEXT, String.format(mContext
                                .getString(R.string.concierge_email),
                                event.getString("name")));
                        try {
                            mContext.startActivity(Intent.createChooser(intent,
                                    String.format(mContext.getString(R.string.email),
                                            event.getString("name"))));
                        } catch (android.content.ActivityNotFoundException e) {
                            // No email clients installed
                        }
                    }
                    else if(event.getString("contactType").equals("phone")) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        Uri data = Uri.parse("sms:"+event.getString("contactInfo"));
                        intent.setData(data);
                        intent.putExtra("sms_body", String.format(mContext
                                .getString(R.string.concierge_sms),
                                event.getString("name")));
                        mContext.startActivity(intent);
                    }


                }
            });
        }
        else {
            TextView header = (TextView) cellView.findViewById(R.id.section_header);
            TextView subHeader = (TextView) cellView.findViewById(R.id.section_subheader);

            header.setText(item.header);
            subHeader.setText("");
        }

        return cellView;
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
        public final String header;

        public Item(ParseObject content) {
            this.type = ITEM;
            this.content = content;
            this.header = "";
        }

        public Item(String header) {
            this.header = header;
            this.type = SECTION;
            this.content = null;
        }

    }
}