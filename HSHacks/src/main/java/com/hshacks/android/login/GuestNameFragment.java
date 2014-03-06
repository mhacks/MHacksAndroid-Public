package com.hshacks.android.login;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hshacks.android.R;

import java.io.File;

/**
 * Created by damian on 1/12/14.
 */
public class GuestNameFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText mEditText;
    private ImageView iconV;
    private static final int CAMERA_REQUEST = 1888;

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
        iconV = (ImageView)  view.findViewById(R.id.userIcon);
        // Make the send button clickable
        view.findViewById(R.id.goUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOn();
            }
        });
        // Get an image of the user
        iconV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = getCircle(ThumbnailUtils.extractThumbnail((Bitmap) data.getExtras().get("data"), 256, 256));
            iconV.setImageBitmap(photo);
            /**
             * YO UPLOAD DIS "PHOTO" SHIT TO PARSE
             */
        }
    }

    public Bitmap getCircle(Bitmap bitmapimg) {
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            goOn();
            return true;
        }
        return false;
    }

    public void goOn() {
        // Return input text to activity
        GuestnameDialogListener activity = (GuestnameDialogListener) getActivity();
        String name = mEditText.getText().toString();
        if(name.length() > 0) {
            activity.onFinishEditDialog(name);
            this.dismiss();
        }else{
            Toast.makeText(getActivity(),"Please enter a username", Toast.LENGTH_SHORT).show();
        }
    }
}
