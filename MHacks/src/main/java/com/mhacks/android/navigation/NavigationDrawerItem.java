package com.mhacks.android.navigation;

import android.app.Fragment;

/**
* Created by damian on 1/12/14.
*/
public class NavigationDrawerItem {
    public String title;
    public int iconID;
    public Fragment fragment;
    public NavigationDrawerItem(Fragment fragment, String title, int iconID) {
        this.title = title;
        this.iconID = iconID;
        this.fragment = fragment;
    }
}
