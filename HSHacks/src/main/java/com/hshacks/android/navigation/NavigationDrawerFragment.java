package com.hshacks.android.navigation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hshacks.android.R;
import com.hshacks.android.time.TimeDelta;
import com.hshacks.android.views.ProgressWheel;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mCountdownLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private List<NavigationDrawerItem> mItems;
    private NavigationDrawerAdapter mAdapter;

    private TimeDelta mTimeLeftDelta;

    private TextView mDaysText;
    private TextView mHoursText;
    private TextView mMinsText;
    private TextView mSecsText;
    private ProgressWheel mSpinner;
    private Timer mCountdown;
    private int mProgress = 0;
    private boolean mAdminEnabled = false;

    private int mCurrentSelectedPosition = 0;

    private final Date START_DATE;
    private final Date END_DATE;

    public NavigationDrawerFragment() {
        mItems = new ArrayList();
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 2, 7, 0, 0);
        START_DATE = cal.getTime();
        cal.set(2014, 2, 8, 9, 0);
        END_DATE = cal.getTime();
    }

    public void setItems(List<NavigationDrawerItem> items) {
        mItems = items;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) layout.findViewById(R.id.drawer_listview);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mCountdownLayout = (RelativeLayout) layout.findViewById(R.id.countdown_layout);

        mDaysText = (TextView) layout.findViewById(R.id.days_remaining);
        mHoursText = (TextView) layout.findViewById(R.id.hours_remaining);
        mMinsText = (TextView) layout.findViewById(R.id.minutes_remaining);
        mSecsText = (TextView) layout.findViewById(R.id.seconds_remaining);

        mSpinner = (ProgressWheel) layout.findViewById(R.id.countdown_spinner);
        mSpinner.setProgress(mProgress);

        if (ParseUser.getCurrentUser().getBoolean("admin"))
            setUpAdminToggle();
        return layout;
    }

    private void setUpAdminToggle() {
        mCountdownLayout.setOnClickListener(new View.OnClickListener() {
            int clicks = 0;
            long prevClickTime = 0;
            @Override
            public void onClick(View view) {
                long time = new Date().getTime();
                if (time - prevClickTime < 250) {
                    clicks++;
                } else {
                    clicks = 1;
                }
                if (clicks == 8) {
                    mAdminEnabled = !mAdminEnabled;
                    clicks = 1;
                    Drawable background = getResources().getDrawable(mAdminEnabled ? R.drawable.app_background : R.color.actionbar_dark_bg);
                    getActionBar().setBackgroundDrawable(background);
                    Toast.makeText(getActivity(), "Admin: " + mAdminEnabled, Toast.LENGTH_SHORT).show();
                }
                prevClickTime = time;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                mTimeLeftDelta = TimeDelta.timeFromNowUntil(END_DATE);
                mProgress = (int) (TimeDelta.progressBetween(START_DATE, END_DATE) * 360);

                NavigationDrawerFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCountdown();
                    }
                });
            }
        };
        mCountdown = new Timer();
        mCountdown.scheduleAtFixedRate(task, 0, 1000);
    }

    public void updateCountdown() {
        mDaysText.setText(buildTimeUnitLabel(mTimeLeftDelta.days, getString(R.string.day)));
        mHoursText.setText(buildTimeUnitLabel(mTimeLeftDelta.hours, getString(R.string.hour)));
        mMinsText.setText(buildTimeUnitLabel(mTimeLeftDelta.minutes, getString(R.string.minute)));
        mSecsText.setText(buildTimeUnitLabel(mTimeLeftDelta.seconds, getString(R.string.second)));
        mSpinner.setProgress(mProgress);
    }

    private String buildTimeUnitLabel(long number, String label) {
        if(number != 1) {
            return number + label + "s";
        } else {
            return number + label;
        }
    }

    @Override
    public void onDestroy() {
        if (mCountdown != null) {
            mCountdown.cancel();
        }
        super.onDestroy();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout
    yout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mAdapter = new NavigationDrawerAdapter(getActivity(), mItems);
        mDrawerListView.setAdapter(mAdapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        mAdapter.setCurrent(mCurrentSelectedPosition);
        mDrawerListView.setSelector(R.drawable.list_selector);

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mDrawerListView.getWindowToken(), 0);
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            mAdapter.setCurrent(position);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}