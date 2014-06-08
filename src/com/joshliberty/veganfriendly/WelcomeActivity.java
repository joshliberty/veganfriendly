package com.joshliberty.veganfriendly;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joshliberty.veganfriendly.api.ApiClient;
import com.joshliberty.veganfriendly.api.RestaurantListCallback;
import com.joshliberty.veganfriendly.models.Restaurant;
import com.joshliberty.veganfriendly.utils.App;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class WelcomeActivity extends Activity implements RestaurantListCallback {

    private static final String LAST_UPDATE = "last_update_timestamp";
    private GoogleMap map;
    private Location userLocation;
    private Location previousUserLocation;

    private CameraPosition mapCenter;
//    private CameraPosition previousMapCenter;

    private Marker userLocationMarker;
    private LocationManager locationManager;

    private HashSet<Restaurant> addedRestaurants = new HashSet<Restaurant>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Bind views
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mapCenter = map.getCameraPosition();
//        // Register listeners
//        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                previousMapCenter = mapCenter;
//                mapCenter = cameraPosition;
//                double distanceMoved = 0;
//                boolean zoomChanged = false;
//                if(previousMapCenter != null){
//                    LatLng target = cameraPosition.target;
//                    LatLng previousTarger = previousMapCenter.target;
//                    distanceMoved = GeoUtil.getDistance(target.latitude, target.longitude,
//                            previousTarger.latitude, previousTarger.longitude);
//                    zoomChanged = previousMapCenter.zoom != mapCenter.zoom;
//                }
//                Log.d("JOSH", "Distance moved is "+String.valueOf(distanceMoved));
//                Log.d("JOSH", "Current zoom "+String.valueOf(mapCenter.zoom));
//                Log.d("JOSH", "Previous zoom "+String.valueOf(previousMapCenter.zoom));
//                if(previousMapCenter == null || zoomChanged || distanceMoved > 150){
//                    getRestaurants();
//                }
//            }
//        });

        // Internal setup
        getLocationService();
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        LocationListener locationListener = new OurLocationListener();
        userLocation = locationManager.getLastKnownLocation(provider);
        if(userLocation == null){
            Toast.makeText(this, getString(R.string.waiting_location), Toast.LENGTH_LONG).show();
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        // Start working
        getRestaurants();
    }

    private void setUserLocation(Location newUserLocation){
        previousUserLocation = userLocation;
        userLocation = newUserLocation;
        LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        if(userLocationMarker == null){
            // Initial setup
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            userLocationMarker = map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.your_location)));
        } else {
            userLocationMarker.setPosition(latLng);
        }
    }

    private void getRestaurants(){
        Log.d(WelcomeActivity.class.getSimpleName(), "Trying to get restaurants");
        long lastUpdate = App.getPreferences(this).getLong(LAST_UPDATE, 0);
        long timeDelta = new Date().getTime() - lastUpdate;
        if(timeDelta > 604800){
            ApiClient.getRestaurantsAsync(this, mapCenter.target.latitude, mapCenter.target.longitude, this);
        }
    }

    private void getLocationService() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enabled){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onSuccess(List<Restaurant> restaurants) {
        Log.d(WelcomeActivity.class.getSimpleName(), "Got restaurants");
        List<Restaurant> knownRestaurants = new Select().from(Restaurant.class).execute();
        App.saveSetting(WelcomeActivity.this, LAST_UPDATE, new Date().getTime());
        for(Restaurant restaurant : restaurants){
            if(!knownRestaurants.contains(restaurant)){
                Log.d(WelcomeActivity.class.getSimpleName(), "Saving new restaurant: "+restaurant.getName());
                restaurant.save();
            }
            LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            if(!addedRestaurants.contains(restaurant)){
                map.addMarker(new MarkerOptions().position(latLng).title(restaurant.getName()));
                addedRestaurants.add(restaurant);
            }
        }
    }

    @Override
    public void onFailure(int error, String description) {
        Toast.makeText(this, getString(R.string.error_getting_restaurants), Toast.LENGTH_LONG).show();
    }

    private class OurLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            setUserLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("JOSH", "Status changed in provider "+provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("JOSH", "Provider enabled "+provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("JOSH", "Provider disabled "+provider);
        }
    }

}
