package com.hshacks.android;

import android.app.Application;

import com.hshacks.android.R;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;

/**
 * Created by damian on 11/15/13.
 */
public class MHacksApp extends Application {
    public void onCreate(){
        super.onCreate();
        // Initialize Parse
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize("PUTYOUROWNAPPIDHERE");
        ParseTwitterUtils.initialize("PUTYOUROWN", "KEYSHERE");
        // end Parse initialization

        // Set up default roles
        ParseACL defaultACL = new ParseACL();
        // Everybody can read objects created by this user
        defaultACL.setPublicReadAccess(true);
        // Moderators can also modify these objects
        defaultACL.setRoleWriteAccess("Administrator", true);
        // And the user can read and modify its own objects
        ParseACL.setDefaultACL(defaultACL, true);
        // End default roles
    }
}
