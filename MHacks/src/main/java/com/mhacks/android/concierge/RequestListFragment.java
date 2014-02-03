package com.mhacks.android.concierge;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mhacks.android.R;

import java.util.List;

/**
 * Created by damian on 1/12/14.
 */
public class RequestListFragment extends DialogFragment {
    private ListView mListView;
    private List<String> mOptions;
    private ListView.OnItemClickListener mListener;
    private String mTitle;

    public RequestListFragment(String title, List<String> options) {
        mOptions = options;
        mListener = null;
        mTitle = title;
    }

    public void setOnItemClickListener(ListView.OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requestlist, container);
        mListView = (ListView) view.findViewById(R.id.request_list);
        mListView.setOnItemClickListener(mListener);
        getDialog().setTitle(mTitle);
        mListView.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mOptions.toArray()));
        return view;
    }
}
