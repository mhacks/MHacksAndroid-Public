package com.hshacks.android.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.hshacks.android.MainActivity;
import com.hshacks.android.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends Activity implements GuestNameFragment.GuestnameDialogListener{
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    private LinearLayout mLoginWelcomeView;
    private TextView mLoginWelcomeText;

    String username = "Anonymous";
    String avatar_url = "http://www.genengnews.com/app_themes/genconnect/images/default_profile.jpg";

    SharedPreferences mPrefs;
    ParseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Check if the user is already logged in
        mUser = ParseUser.getCurrentUser();
        if (mUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("login", MODE_PRIVATE);

        setContentView(R.layout.activity_login);
        getActionBar().hide();

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        mLoginWelcomeView = (LinearLayout) findViewById(R.id.login_welcome);
        mLoginWelcomeText = (TextView) findViewById(R.id.login_welcome_message);

        findViewById(R.id.fb_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptFBLogin();
            }
        });
        findViewById(R.id.twitter_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptTwitterLogin();
            }
        });
        findViewById(R.id.guest_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGuestLogin();
            }
        });

        showWelcome(true);
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWelcome(false);
                    }
                });
            }
        };
        Timer delay = new Timer();
        delay.schedule(task, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void attemptGuestLogin() {
        FragmentManager fm = getFragmentManager();
        GuestNameFragment dialog = new GuestNameFragment();
        dialog.show(fm, "guestname_dialog");
    }

    @Override
    public void onFinishEditDialog(final String inputText) {
        showProgress(true);
        ParseUser.logInInBackground(inputText, "testpass1", new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    setupAndComplete();
                } else {
                    attemptGuestRegistration(inputText);
                }
            }
        });
    }

    public void attemptGuestRegistration(String inputText) {
        ParseUser user = new ParseUser();
        user.setUsername(inputText);
        user.setPassword("testpass1");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    setupAndComplete();
                } else {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, getString(R.string.error_guestname), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void attemptFBLogin() {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
        showProgress(true);
        ParseFacebookUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    showProgress(false);
                    Log.d("Facebook", "User is null!");
                } else {
                    setupAndComplete();
                    Log.d("FacebookLogin", user.toString());
                }
            }
        });
    }

    //TODO: toast error on failed login

    public void attemptTwitterLogin() {
        View focusView = null;
        mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
        showWelcome(false);
        showProgress(true);
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    showProgress(false);
                } else {
                    setupAndComplete();
                }
            }
        });
    }

    private void completeLogin() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("username", username);
        editor.putString("avatar_url", avatar_url);
        editor.commit();
        showProgress(false);
        showWelcome(true);
        mLoginWelcomeText.setText(getString(R.string.login_welcome_post) + username);
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };
        Timer delay = new Timer();
        delay.schedule(task, 2000);
    }

    private void setupAndComplete() {
        final Runnable completer = new Runnable() {
            @Override
            public void run() {
                completeLogin();
            }
        };
        Log.d("Facebook", String.valueOf(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())));
        if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    username = user.getName();
                    avatar_url = "http://graph.facebook.com/" + user.getId() + "/picture?type=square";
                    LoginActivity.this.runOnUiThread(completer);
                }
            });
            request.executeAsync();
        }
        else if (ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())) {
            username = "@" + ParseTwitterUtils.getTwitter().getScreenName();
            AsyncTask avatar_task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet verifyGet = new HttpGet(
                            "https://api.twitter.com/1.1/account/verify_credentials.json");
                    ParseTwitterUtils.getTwitter().signRequest(verifyGet);
                    try {
                        HttpResponse response = client.execute(verifyGet);
                        InputStream stream = response.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null)
                        {
                            sb.append(line + "\n");
                        }
                        JSONObject profile = new JSONObject(sb.toString());
                        Log.d("Twitter", sb.toString());
                        avatar_url = profile.getString("profile_image_url");
                    } catch (Exception e) {
                        Log.d("Twitter", e.toString());
                        // TODO: add a toast error? logout?
                        // do nothing, stay anonymous
                    }
                    LoginActivity.this.runOnUiThread(completer);
                    return "";
                }
            };
            avatar_task.execute();
        }
        else if (ParseUser.getCurrentUser().isAuthenticated()) {
            username = ParseUser.getCurrentUser().getUsername();
            runOnUiThread(completer);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showWelcome(final boolean show) {
        showProgress(false);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

            mLoginWelcomeView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginWelcomeView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginWelcomeView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginWelcomeView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
