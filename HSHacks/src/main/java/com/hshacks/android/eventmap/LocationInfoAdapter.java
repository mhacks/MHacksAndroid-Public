package com.hshacks.android.eventmap;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.hshacks.android.R;

/**
 * Created by damian on 1/14/14.
 */
public class LocationInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity mActivity;

    public LocationInfoAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }


    @Override
    public View getInfoContents(Marker marker) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.locationinfo_window, null);
        TextView title = (TextView) view.findViewById(R.id.locationinfo_title);
        TextView snippet = (TextView) view.findViewById(R.id.locationinfo_snippet);

        title.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());

        return view;
    }
}
