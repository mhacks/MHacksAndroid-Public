package com.hshacks.android.schedule;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hshacks.android.R;
import com.hshacks.android.pong.PongHeaderTransformer;import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ScheduleFragment extends Fragment implements OnRefreshListener {
    private ScheduleAdapter mAdapter;
    private ArrayList<ParseObject> mEvents;

    private ListView mListView;
    private PullToRefreshLayout mPullToRefreshLayout;

    public ScheduleFragment() {
        mEvents = new ArrayList<ParseObject>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_schedule, container, false);
        mListView = (ListView) layout.findViewById(R.id.schedule_listview);
        mListView.addFooterView(new View(getActivity()));

        mAdapter = new ScheduleAdapter(this.getActivity(), mEvents);
        mListView.setAdapter(mAdapter);

        View view = new View(getActivity());
        int pixels = 48 * getResources().getDisplayMetrics().densityDpi / 160;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels);
        view.setLayoutParams(params);
        mListView.addFooterView(view);

        setRetainInstance(true);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewGroup container = (ViewGroup) view;

        mPullToRefreshLayout = new PullToRefreshLayout(container.getContext());
        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create()
                        .scrollDistance(0.5f)
                        .headerLayout(R.layout.pong_header)
                        .headerTransformer(new PongHeaderTransformer())
                        .build())
                .insertLayoutInto(container)
                .theseChildrenArePullable(R.id.schedule_listview)
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter.getCount() == 0) {
            refresh();
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.orderByAscending("time");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.d("Schedule", "Got " + parseObjects.size() + " events.");
                    mEvents.clear();
                    mEvents.addAll(parseObjects);
                    mAdapter.notifyDataSetChanged();
                }
                mPullToRefreshLayout.setRefreshComplete();
            }
        });
    }

    private void refresh() {
        mPullToRefreshLayout.setRefreshing(true);
        onRefreshStarted(mListView);
    }
}