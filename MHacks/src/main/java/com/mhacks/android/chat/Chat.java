package com.mhacks.android.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {
    private String message;
    private String user;
    private String image;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() { }

    Chat(String message, String author, String imageUrl) {
        this.message = message;
        this.user = author;
        this.image = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public String getImage() {
        return image;
    }

    // determines whether this instance is posted by Dave Fontenot or one of his disciples
    public boolean heKnows() {
        Pattern p = Pattern.compile("(?i)HEL+ *YEA");
        Matcher m = p.matcher(message);
        return m.find();
    }
}
