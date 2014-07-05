package com.joshliberty.veganfriendly;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.joshliberty.veganfriendly.api.FileDownloader;
import com.joshliberty.veganfriendly.models.Restaurant;
import com.joshliberty.veganfriendly.utils.App;
import com.joshliberty.veganfriendly.utils.DialogUtil;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;

@SuppressWarnings("unchecked")
public class MapActivity extends Activity implements Callback<List<Restaurant>>,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String LAST_UPDATE = "last_update_timestamp";

    private GoogleMap map;
    private Location userLocation;
    private CameraPosition mapCenter;

    List<Restaurant> restaurantsInDb = new ArrayList<Restaurant>();
    HashMap<Marker, Restaurant> markersOnMap = new HashMap<Marker, Restaurant>();
    FileDownloader fileDownloader;
    private LocationClient locationClient;
    private boolean initialMoveDone;
    private boolean gettingRestaurants;
    private boolean active = false;

    // Android Framework Callbacks
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        fileDownloader = new FileDownloader(this);
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        if(savedInstanceState == null){
            mapFragment.setRetainInstance(true);
        } else {
            initialMoveDone = true;
            map = mapFragment.getMap();
        }
    }
    protected void onResume(){
        super.onResume();
        setupMap();
        setupLocationClient();
        if(!gettingRestaurants && (restaurantsInDb == null || restaurantsInDb.size() == 0)){
            getRestaurants();
        } else {
            showRestaurantsOnMap();
        }

    }
    protected void onStart(){
        super.onStart();
        active = true;
    }
    protected void onStop(){
        super.onStop();
        active = false;
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
        }
    }
    private void startLocationClient(){
        Log.d(MapActivity.class.getSimpleName(), "in StartLocationClient.");
        if(locationClient == null){
            Log.d(MapActivity.class.getSimpleName(), "Location client is null.");
            locationClient = new LocationClient(this, this, this);
        }
        if(!locationClient.isConnected()){
            Log.d(MapActivity.class.getSimpleName(), "Location client isn't connected.");
            locationClient.connect();
        }
    }
    private boolean isLocationEnabled(){
        int apiVer = Build.VERSION.SDK_INT;

        if(apiVer >= 19){
            try {
                int locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                if (Settings.Secure.LOCATION_MODE_OFF == locationMode){
                    return false;
                }
            } catch (Settings.SettingNotFoundException e) {
                Log.e(MapActivity.class.getSimpleName(), "Error when checking whether location is enabled.", e);
                return false;
            }
        } else {
            String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(locationProviders == null || locationProviders.equals("")){
                return false;
            }
        }

        return true;

    }
    private void setupLocationClient() {

        if(isLocationEnabled()){
            startLocationClient();
        } else {
            Log.d(MapActivity.class.getSimpleName(), "Showing location dialog.");
            DialogUtil.showDialog(this, R.string.no_location,
                    R.string.enable_location,
                    R.string.go_to_settings,
                    R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    },
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.d(MapActivity.class.getSimpleName(), "Dialog cancelled. We'll get restaurants");
                            dialog.dismiss();
                        }
                    });
        }
    }

    // Connection to API
    private void handleRetrievedRestaurants(List<Restaurant> restaurants) {
        new AsyncTask<List<Restaurant>, Void, Void>(){
            List<Restaurant> restaurantsToAdd = new ArrayList<Restaurant>();

            @Override
            protected Void doInBackground(List<Restaurant>... params) {
                Log.d(MapActivity.class.getSimpleName(), "Got restaurants");
                App.saveSetting(MapActivity.this, LAST_UPDATE, new Date().getTime());
                for(Restaurant restaurant : params[0]){
                    if(!restaurantsInDb.contains(restaurant)){
                        Log.d(MapActivity.class.getSimpleName(), "Saving new restaurant: " + restaurant.getName());
                        restaurant.save();
                        restaurantsToAdd.add(restaurant);
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result){
                Log.d(MapActivity.class.getSimpleName(), "onPostExecute");
                Log.d(MapActivity.class.getSimpleName(), "Starting to process queue");
                restaurantsInDb.addAll(restaurantsToAdd);
                showRestaurantsOnMap();
                fileDownloader.fetchMissingImages();
                gettingRestaurants = false;
            }
        }.execute(restaurants);


    }

    // Restaurants on map
    private void openRestaurant(Marker marker){
        Restaurant restaurant = markersOnMap.get(marker);
        Intent intent = new Intent(MapActivity.this, RestaurantActivity.class);
        intent.putExtra(RestaurantActivity.RESTAURANT_ID, restaurant.getId());
        startActivity(intent);
    }
    private void setupMap(){
        if(map == null){
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (map != null){
                map.setInfoWindowAdapter(new InfoWindow(getLayoutInflater(), markersOnMap, this));
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        openRestaurant(marker);
                    }
                });
            }
        }
        map.setMyLocationEnabled(true);
        mapCenter = map.getCameraPosition();
        map.setOnCameraChangeListener(new OurCameraChangedListener());

    }
    private void addRestaurant(Restaurant restaurant){
        LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
        if(!markersOnMap.values().contains(restaurant)){
            BitmapDescriptor descriptor;
            if(restaurant.isIs_vegan()){
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_vegan);
            } else {
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_default);
            }
            MarkerOptions options = new MarkerOptions().position(latLng).title(restaurant.getName()).icon(descriptor);
            Marker m = map.addMarker(options);
            markersOnMap.put(m, restaurant);
        }
    }
    private void getRestaurants(){
        gettingRestaurants = true;
        Log.d(MapActivity.class.getSimpleName(), "Loading restaurants from db");
        restaurantsInDb = new Select().from(Restaurant.class).execute();
        Log.d(MapActivity.class.getSimpleName(), "Got "+ restaurantsInDb.size()+" restaurants");
        long lastUpdate = App.getPreferences(this).getLong(LAST_UPDATE, 0);
        long timeDelta = new Date().getTime() - lastUpdate;
        if(timeDelta > 604800000){ // update weekly in any case todo add gcm receiver to receive push updates
            Log.d(MapActivity.class.getSimpleName(), "Trying to get restaurants from server");
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ApiService.API_SERVER).build();
            ApiService service = restAdapter.create(ApiService.class);
            service.getRestaurants(this);
        } else {
            gettingRestaurants = false;
        }
        showRestaurantsOnMap();
        fileDownloader.fetchMissingImages();
    }
    private void showRestaurantsOnMap(){
        Log.d(MapActivity.class.getSimpleName(), "Showing restaurants in map");
        Log.d(MapActivity.class.getSimpleName(), "Zoom level is "+mapCenter.zoom);

        if(mapCenter.zoom > 12){
            Log.d(MapActivity.class.getSimpleName(), "Zoom level is sufficient");
            LatLngBounds mapBounds = map.getProjection().getVisibleRegion().latLngBounds;
            for(Restaurant restaurant : restaurantsInDb){
                if(!markersOnMap.values().contains(restaurant)){
                    LatLng restaurantLocation = new LatLng(restaurant.latitude, restaurant.longitude);
                    boolean display = mapBounds.contains(restaurantLocation);
                    if(display){
                        addRestaurant(restaurant);
                    }
                }
            }
        }
    }

    // ApiService Callbacks
    public void success(List<Restaurant> restaurants, Response response) {
        handleRetrievedRestaurants(restaurants);
    }
    public void failure(RetrofitError error) {
        gettingRestaurants = false;
        String err;
        if(error == null || error.getResponse() == null || error.getBody() == null){
            err = "Unknown Error";
        } else {
            err = error.getBody().toString();
        }
        Toast.makeText(this, getString(R.string.error_getting_restaurants), Toast.LENGTH_LONG).show();

        if(active){
            DialogUtil.showDialog(this, R.string.error,
                    R.string.error_getting_restaurants,
                    R.string.retry,
                    R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getRestaurants();
                            dialog.dismiss();
                        }
                    },
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.d(MapActivity.class.getSimpleName(), "Dialog cancelled.");
                            dialog.dismiss();
                        }
                    });
        }
    }

    // Location Service Callbacks
    public void onConnected(Bundle bundle) {
        Log.d(MapActivity.class.getSimpleName(), "in OnConnected");
        if(locationClient != null && locationClient.isConnected()){
            Log.d(MapActivity.class.getSimpleName(), "Location client not null and is connected!");
            Location lastLocation = locationClient.getLastLocation();
            if(lastLocation != null){
                Log.d(MapActivity.class.getSimpleName(), "Last location is good to go.");
                setUserLocation(lastLocation);
            }
            LocationRequest request = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationClient.requestLocationUpdates(request, new OurLocationListener());
        } else {
            setupLocationClient();
        }
    }
    public void onDisconnected() {

    }
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // Location Updates Listener
    private class OurCameraChangedListener implements GoogleMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(final CameraPosition cameraPosition) {
            Log.d(MapActivity.class.getSimpleName(), "Camera position has changed.");
            mapCenter = cameraPosition;
            showRestaurantsOnMap();
        }

    }
    private class OurLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            setUserLocation(location);
        }

    }

}
