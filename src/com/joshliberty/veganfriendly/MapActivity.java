package com.joshliberty.veganfriendly;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import com.joshliberty.veganfriendly.api.ApiService;
import com.joshliberty.veganfriendly.models.Restaurant;
import com.joshliberty.veganfriendly.utils.App;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;

public class MapActivity extends Activity implements Callback<List<Restaurant>>,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String LAST_UPDATE = "last_update_timestamp";

    private GoogleMap map;
    private Location userLocation;
    private CameraPosition mapCenter;

    List<Restaurant> knownRestaurants = new ArrayList<Restaurant>();
    HashMap<Marker, Restaurant> markers = new HashMap<Marker, Restaurant>();
    private HashSet<Restaurant> addedRestaurants = new HashSet<Restaurant>();
    private LocationClient locationClient;
    private boolean initialMoveDone;

    // Android Framework Callbacks
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        if(savedInstanceState == null){
            mapFragment.setRetainInstance(true);
        } else {
            initialMoveDone = true;
            map = mapFragment.getMap();
        }
        // Init map
        setupMap();

        // Internal setup
        setupLocationClient();
    }
    protected void onResume(){
        super.onResume();
        setupMap();
    }
    public void onClick(View v) {
        switch (v.getId()){
            default:
                // Do something
        }
    }

    // User and Map location
    private void moveCameraToLocation(double latitude, double longitude){
        LatLng latLng = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    private void setUserLocation(Location location){
        userLocation = location;
        if(!initialMoveDone){
            initialMoveDone = true;
            moveCameraToLocation(userLocation.getLatitude(), userLocation.getLongitude());
            getRestaurants();
        }
    }
    private void setupLocationClient() {
        locationClient = new LocationClient(this, this, this);
        locationClient.connect();
    }

    // Restaurants on map
    private void setupMap(){
        if(map == null){
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (map != null){
                map.setMyLocationEnabled(true);
                mapCenter = map.getCameraPosition();
                map.setOnCameraChangeListener(new OurCameraChangedListener());
            }
        }
    }
    private void clearMap(){
        markers.clear();
        addedRestaurants.clear();
        map.clear();
    }
    private void addRestaurant(Restaurant restaurant){
        LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
        if(!addedRestaurants.contains(restaurant)){
            BitmapDescriptor descriptor;
            if(restaurant.isIs_vegan()){
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_vegan);
            } else {
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_default);
            }
            MarkerOptions options = new MarkerOptions().position(latLng).title(restaurant.getName()).icon(descriptor);
            Marker m = map.addMarker(options);
            markers.put(m, restaurant);
            addedRestaurants.add(restaurant);
        }
    }
    private void getRestaurants(){
        knownRestaurants = new Select().from(Restaurant.class).execute();
        Log.d(MapActivity.class.getSimpleName(), "Trying to get restaurants");
        long lastUpdate = App.getPreferences(this).getLong(LAST_UPDATE, 0);
        long timeDelta = new Date().getTime() - lastUpdate;
        if(timeDelta > 604800000){ // update weekly in any case todo add gcm receiver to receive push updates
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiService.API_SERVER).build();
            ApiService service = restAdapter.create(ApiService.class);
            service.getRestaurants(mapCenter.target.latitude, mapCenter.target.longitude, this);
        } else {
            showRestaurantsOnMap();
        }
    }
    @SuppressWarnings("unchecked")
    private void handleRetrievedRestaurants(List<Restaurant> restaurants) {
        new AsyncTask<List<Restaurant>, Void, Void>(){
            List<Restaurant> restaurantsToAdd = new ArrayList<Restaurant>();
            @Override
            protected Void doInBackground(List<Restaurant>... params) {
                Log.d(MapActivity.class.getSimpleName(), "Got restaurants");
                App.saveSetting(MapActivity.this, LAST_UPDATE, new Date().getTime());
                for(Restaurant restaurant : params[0]){
                    if(!knownRestaurants.contains(restaurant)){
                        Log.d(MapActivity.class.getSimpleName(), "Saving new restaurant: "+restaurant.getName());
                        restaurant.save();
                        restaurantsToAdd.add(restaurant);
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result){
                knownRestaurants.addAll(restaurantsToAdd);
                showRestaurantsOnMap();
            }
        }.execute(restaurants);


    }
    private void showRestaurantsOnMap(){
        LatLngBounds mapBounds = map.getProjection().getVisibleRegion().latLngBounds;
        clearMap();
        for(Restaurant restaurant : knownRestaurants){
            LatLng restaurantLocation = new LatLng(restaurant.latitude, restaurant.longitude);
            boolean display = mapBounds.contains(restaurantLocation);
            if(display){
                addRestaurant(restaurant);
            }
        }
    }

    // ApiService Callbacks
    public void success(List<Restaurant> restaurants, Response response) {
        handleRetrievedRestaurants(restaurants);
    }
    public void failure(RetrofitError error) {
        Toast.makeText(this, getString(R.string.error_getting_restaurants)+error.getBody(), Toast.LENGTH_LONG).show();
    }

    // Location Service Callbacks
    public void onConnected(Bundle bundle) {
        Location lastLocation = locationClient.getLastLocation();
        if(lastLocation != null){
            setUserLocation(lastLocation);
        }
        LocationRequest request = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationClient.requestLocationUpdates(request, new OurLocationListener());
    }
    public void onDisconnected() {

    }
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // Location Updates Listener
    private class OurCameraChangedListener implements GoogleMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(final CameraPosition cameraPosition) {
            if(cameraPosition.zoom > 12){
                showRestaurantsOnMap();
            }
        }

    }
    private class OurLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            setUserLocation(location);
        }

    }

}
