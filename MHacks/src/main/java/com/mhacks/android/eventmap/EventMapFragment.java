package com.mhacks.android.eventmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by damian on 1/14/14.
 */
public class EventMapFragment extends MapFragment {
    private View mView;
    private int mPadding;
    private GoogleMap mMap;
    private Activity mActivity;
    private LatLngBounds mVenue;
    private ArrayList<Marker> mMarkers;
    private LocationInfoAdapter mAdapter;

    public EventMapFragment() { super(); }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        mActivity = getActivity();
        mMap = getMap();
        mMarkers = new ArrayList();
        mAdapter = new LocationInfoAdapter(mActivity);
        mPadding = 80 * mActivity.getResources().getDisplayMetrics().densityDpi / 160;
        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi") // We check which build version we are using.
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                loadData();
            }
        });
        return mView;
    }

    private void loadData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MapLocation");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    initMap(parseObjects);
                }
            }
        });
    }

    private void initMap(List<ParseObject> parseObjects) {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        mMap.setInfoWindowAdapter(mAdapter);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ParseObject object : parseObjects) {
            LatLng coordinates = getLatLng(object.getParseGeoPoint("coordinates"));
            MarkerOptions options = new MarkerOptions()
                    .position(coordinates)
                    .title(object.getString("title"))
                    .snippet(object.getString("snippet"));
            mMarkers.add(mMap.addMarker(options));
            builder.include(coordinates);
        }
        mVenue = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mVenue, mPadding));
    }

    private LatLng getLatLng(ParseGeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }
}
