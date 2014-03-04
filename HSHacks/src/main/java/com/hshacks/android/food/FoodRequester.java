package com.hshacks.android.food;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.hshacks.android.R;
import com.hshacks.android.concierge.RequestListFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boztalay on 1/13/14.
 */
public class FoodRequester {
    private static final int FOOD_REQUEST = 0;
    private static final int DRINK_REQUEST = 1;

    private ArrayList<String> foodItems;
    private ArrayList<String> drankItems;

    private Activity parentActivity;

    public FoodRequester(Activity parentActivity) {
        foodItems = new ArrayList<String>();
        drankItems = new ArrayList<String>();

        this.parentActivity = parentActivity;
    }

    public void showFoodRequestDialog() {
        refreshFoodAndDrinkItemsAndShowRequestDialog(FOOD_REQUEST);
    }

    public void showDrinkRequestDialog() {
        refreshFoodAndDrinkItemsAndShowRequestDialog(DRINK_REQUEST);
    }

    private void refreshFoodAndDrinkItemsAndShowRequestDialog(final int requestType) {
        final ProgressDialog progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(parentActivity.getString(R.string.fooddrink_loading));
        progressDialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodDrink");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.d("Food", "Got " + parseObjects.size() + " food/drink items.");

                    progressDialog.cancel();

                    processFoodDrinkQueryResponse(parseObjects);
                }
            }
        });
    }

    private void processFoodDrinkQueryResponse(List<ParseObject> parseObjects) {
        foodItems.clear();
        drankItems.clear();

        for (ParseObject parseObject : parseObjects) {
            if (parseObject.getBoolean("available")) {
                String itemType = parseObject.getString("type");
                String itemName = parseObject.getString("name");

                if (itemType.equals("food")) {
                    foodItems.add(itemName);
                } else if (itemType.equals("drink")) {
                    drankItems.add(itemName);
                }
            }
        }
    }

}
