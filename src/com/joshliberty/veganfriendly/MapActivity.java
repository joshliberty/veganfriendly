package com.joshliberty.veganfriendly;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.joshliberty.veganfriendly.api.RestaurantListCallback;
import com.joshliberty.veganfriendly.models.Restaurant;

import java.util.List;

/**
 * Created by caligula on 05/06/14.
 */
public class MapActivity extends Activity implements RestaurantListCallback {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    @Override
    public void onSuccess(List<Restaurant> restaurants) {
        Toast.makeText(this, "Got restaurants", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(int error, String description) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
    }
}