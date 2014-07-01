package com.joshliberty.veganfriendly;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.joshliberty.veganfriendly.api.FileDownloader;
import com.joshliberty.veganfriendly.models.Restaurant;

import java.io.File;
import java.util.HashMap;

/**
 * Created by caligula on 28/06/14.
 * This file is part of VeganFriendly.
 */
public class InfoWindow implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater layoutInflater;
    private HashMap<Marker, Restaurant> restaurants;
    private Context mContext;

    InfoWindow(LayoutInflater inflater, HashMap<Marker, Restaurant> restaurants, Context context){
        layoutInflater = inflater;
        this.restaurants = restaurants;
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Restaurant rest = restaurants.get(marker);
        View popup = layoutInflater.inflate(R.layout.info_window, null);

        ImageView image = (ImageView) popup.findViewById(R.id.imageView);
        TextView title = (TextView) popup.findViewById(R.id.restaurant_name);
        TextView address = (TextView) popup.findViewById(R.id.restaurant_address);
        TextView phone = (TextView) popup.findViewById(R.id.restaurant_phone);
        TextView style = (TextView) popup.findViewById(R.id.restaurant_style);
        File file = new File(mContext.getFilesDir(), rest.getImage_name());

        if (file.exists()){
            image.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        address.setText(rest.getAddress());
        phone.setText(rest.getPhone());
        style.setText(rest.getCuisine());
        title.setText(rest.getName());

        return popup;
    }

}
