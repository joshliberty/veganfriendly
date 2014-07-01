package com.joshliberty.veganfriendly;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.activeandroid.query.Select;
import com.joshliberty.veganfriendly.models.Restaurant;

import java.io.File;
import java.util.List;

/**
 * Created by caligula on 01/07/14.
 * This file is part of VeganFriendly.
 */
public class RestaurantActivity extends Activity implements View.OnClickListener {
    public static final String RESTAURANT_ID = "ID";

    private Restaurant restaurant;

    private TextView restaurant_name;
    private TextView restaurant_phone;
    private TextView restaurant_address;
    private TextView restaurant_description;
    private TextView restaurant_cuisine;
    private Button button_navigate;
    private Button button_call;
    private ImageView restaurant_image;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.bottom_to_top, R.anim.fade_out_slow);
        setContentView(R.layout.activity_restaurant);
        setupRestaurant();
        bindViews();
        populateViews();
    }

    private void bindViews(){
        restaurant_name = (TextView) findViewById(R.id.rest_name);
        restaurant_phone = (TextView) findViewById(R.id.rest_phone);
        restaurant_address = (TextView) findViewById(R.id.rest_address);
        restaurant_description = (TextView) findViewById(R.id.rest_desc);
        restaurant_cuisine = (TextView) findViewById(R.id.rest_cuisine);
        button_navigate = (Button) findViewById(R.id.navigate);
        button_call = (Button) findViewById(R.id.call);
        restaurant_image = (ImageView) findViewById(R.id.rest_image);
    }
    private void populateViews(){
        restaurant_name.setText(restaurant.getName());
        restaurant_phone.setText(restaurant.getPhone());
        restaurant_address.setText(restaurant.getAddress());
        restaurant_description.setText(restaurant.getDescription());
        restaurant_cuisine.setText(restaurant.getCuisine());

        File file = new File(getFilesDir(), restaurant.getImage_name());
        if (file.exists()){
            restaurant_image.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        button_navigate.setOnClickListener(this);
        button_call.setOnClickListener(this);

    }
    private void setupRestaurant(){
        long rest_id = (Long) getIntent().getExtras().get(RESTAURANT_ID);
        List<Restaurant> rests = new Select().from(Restaurant.class).where("id = " + rest_id).execute();
        if(rests.size() > 0){
            restaurant = rests.get(0);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navigate:
                String encodedAddress = Uri.encode(restaurant.getAddress());
                Intent intent;
                try { // Try waze first for navigation
                    String url = "waze://?q="+encodedAddress;
                    intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                } catch(ActivityNotFoundException ex){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+encodedAddress));
                }
                startActivity(intent);
                break;
            case R.id.call:
                String phone = restaurant.getPhone();
                String uri = "tel:" + phone.trim() ;
                Intent call_intent = new Intent(Intent.ACTION_CALL);
                call_intent.setData(Uri.parse(uri));
                startActivity(call_intent);
        }
    }
}