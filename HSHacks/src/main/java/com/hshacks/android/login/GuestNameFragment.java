package com.hshacks.android.login;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hshacks.android.R;

/**
 * Created by damian on 1/12/14.
 */
public class GuestNameFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText mEditText;

    public GuestNameFragment() {
        // Empty constructor required for DialogFragment
    }

    public interface GuestnameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guestname, container);
        mEditText = (EditText) view.findViewById(R.id.guestname_text);
        mEditText.setOnEditorActionListener(this);
        getDialog().setTitle(getString(R.string.select_username));

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            GuestnameDialogListener activity = (GuestnameDialogListener) getActivity();
            activity.onFinishEditDialog(mEditText.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }
}
