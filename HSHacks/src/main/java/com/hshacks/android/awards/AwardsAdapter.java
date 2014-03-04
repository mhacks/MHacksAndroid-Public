package com.hshacks.android.awards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hshacks.android.R;
import com.parse.ParseObject;

import java.util.ArrayList;

public class AwardsAdapter extends ArrayAdapter<ParseObject> {
    private Context mContext;
    private ArrayList<ParseObject> mAwards;

    public AwardsAdapter(Context context, ArrayList<ParseObject> announcements) {
        super(context, R.layout.award_cell);

        mContext = context;
        mAwards = announcements;
    }

    public int getCount() {
        return mAwards.size();
    }

    public ParseObject getItem(int position) {
        return mAwards.get(position);
    }

    public long getItemId(int position) {
        return mAwards.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View awardCellView;
        if (convertView == null) {
            awardCellView = inflater.inflate(R.layout.award_cell, null);
        } else {
            awardCellView = convertView;
        }

        TextView awardTitle = (TextView) awardCellView.findViewById(R.id.award_title);
        awardTitle.setText(mAwards.get(position).getString("title"));


        TextView awardBody = (TextView) awardCellView.findViewById(R.id.award_description);
        awardBody.setText(mAwards.get(position).getString("details"));

        TextView awardPrize = (TextView) awardCellView.findViewById(R.id.award_prize);
        awardPrize.setText(mAwards.get(position).getString("award"));

        return awardCellView;
    }

    //Disables selection
    public boolean isEnabled(int position) {
        return false;
    }
}