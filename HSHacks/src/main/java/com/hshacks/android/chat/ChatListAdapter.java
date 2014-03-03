package com.hshacks.android.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.client.Query;
import com.hshacks.android.R;

public class ChatListAdapter extends FirebaseListAdapter<Chat> {
    private ProfilePictureCache profilePictureCache;

    public ChatListAdapter(Query ref, Activity activity, int layout) {
        super(ref, Chat.class, layout, activity);

        profilePictureCache = new ProfilePictureCache();
    }

    @Override
    protected void populateView(View view, Chat chat) {
        // Map a Chat object to an entry in our listview

        ImageView authorImageView = (ImageView) view.findViewById(R.id.chat_author_image);
        String imageUrl = chat.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            profilePictureCache.loadImageURLIntoImageView(imageUrl, authorImageView);
        }

        TextView authorText = (TextView) view.findViewById(R.id.chat_author);
        authorText.setText(chat.getUser());

        ((TextView) view.findViewById(R.id.chat_message)).setText(chat.getMessage());
    }

    //Disables selection
    public boolean isEnabled(int position) {
        return false;
    }
}
