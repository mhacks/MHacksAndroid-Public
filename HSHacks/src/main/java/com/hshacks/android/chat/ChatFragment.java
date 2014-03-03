package com.hshacks.android.chat;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hshacks.android.R;

public class ChatFragment extends ListFragment {
    private static final String FIREBASE_URL = "https://mhacks-chat.firebaseio.com";
    private static final int MAX_VISIBLE_CHATS = 50;

    private String username;
    private String avatar_url;
    private Firebase firebase;
    private ValueEventListener connectedListener;
    private ProgressBar connectionStatusThrobber;
    private ChatListAdapter chatListAdapter;
    private EditText inputText;
    private RelativeLayout mLayout;

    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_chat, null);

        // Make sure we have a username
        mSharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        username = mSharedPreferences.getString("username", "Anonymous");
        avatar_url = mSharedPreferences.getString("avatar_url", "http://www.genengnews.com/app_themes/genconnect/images/default_profile.jpg");

        // Setup our Firebase
        firebase = new Firebase(FIREBASE_URL);

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        inputText = (EditText) view.findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });
        inputText.requestFocus();

        view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        connectionStatusThrobber = (ProgressBar) view.findViewById(R.id.chat_connection_status_throbber);

        mSharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        setRetainInstance(true);

        mLayout = (RelativeLayout) view;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        chatListAdapter = new ChatListAdapter(firebase.limit(MAX_VISIBLE_CHATS), getActivity(), R.layout.chat_message);
        listView.setAdapter(chatListAdapter);
        chatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        connectedListener = firebase.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    connectionStatusThrobber.setVisibility(View.INVISIBLE);
                } else {
                    connectionStatusThrobber.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // Nothing
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.getRoot().child(".info/connected").removeEventListener(connectedListener);
        chatListAdapter.cleanup();
    }

    private void sendMessage() {
        String rawInput = inputText.getText().toString();
        String trimmedInput = rawInput.trim();
        if (!trimmedInput.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(trimmedInput, username, avatar_url);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            firebase.push().setValue(chat);
            inputText.setText("");
        } else {
            inputText.setText("");
        }
    }

    public void putDave() {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = 68 * metrics.densityDpi / 160;
        final ImageView view = new ImageView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        view.setLayoutParams(params);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setAdjustViewBounds(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.removeView(view);
            }
        });

        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight()/2);

        float fromX = 0.0f, fromY = 0.0f;
        double random = Math.random();
        if (random < 0.5) {
            if (random < 0.25) {
                Log.d("HellYeah", "Aligned bottom...");
                fromY = size;
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                view.setImageResource(R.drawable.popup_dave_bottom);
            } else {
                Log.d("HellYeah", "Aligned top...");
                fromY = -size;
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                view.setImageResource(R.drawable.popup_dave_top);
            }
            params.leftMargin = (int) (Math.random() * (mLayout.getWidth() - size));
            Log.d("HellYeah", "Left margin: " + params.leftMargin);
        } else {
            if (random > 0.75) {
                Log.d("HellYeah", "Aligned left...");
                fromX = -size;
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                view.setImageResource(R.drawable.popup_dave_left);
            } else {
                Log.d("HellYeah", "Aligned right...");
                fromX = size;
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                view.setImageResource(R.drawable.popup_dave_right);
            }
            params.topMargin = (int) (Math.random() * (mLayout.getHeight() - size));
            Log.d("HellYeah", "Top margin: " + params.topMargin);
        }

        final TranslateAnimation animation = new TranslateAnimation(
                Animation.ABSOLUTE, fromX,
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, fromY,
                Animation.ABSOLUTE, 0.0f);
        animation.setDuration(500);
        animation.setInterpolator(new BounceInterpolator());
        view.setAnimation(animation);
        mLayout.addView(view);
    }
}
