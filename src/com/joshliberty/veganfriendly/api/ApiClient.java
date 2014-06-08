package com.joshliberty.veganfriendly.api;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;
import com.joshliberty.veganfriendly.R;
import com.joshliberty.veganfriendly.models.Restaurant;
import com.joshliberty.veganfriendly.utils.NetworkUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: caligula
 * Date: 23/11/13
 * Time: 15:17
 */
public class ApiClient {

    public static final String WEBSERVICE = "http://10.0.0.7:5000";
    public static final String GET_RESTAURANTS = "/restaurants/";

    private static final int GENARAL_CONNECTION_TIMEOUT = 9000;
    private static final int GENERAL_SOCKET_TIMEOUT = 25000;

    // Arguments
    public static final String ARG_NAME_LATITUDE = "latitude";
    public static final String ARG_NAME_LONGITUDE = "longitude";

    // Operation IDs
    public static final String OPERATION_ID = "operationId";
    public static final int OPERATION_GET_RESTAURANTS = 1;

    // Listeners
    public static RestaurantListCallback mRestaurantListCallback;

    // Public API to make asynchronous API calls
    public static void getRestaurantsAsync(Context context, double longitude, double latitude,
                                           RestaurantListCallback listener){
        mRestaurantListCallback = listener;
        Bundle args = new Bundle();
        args.putInt(OPERATION_ID, OPERATION_GET_RESTAURANTS);
        args.putString(ARG_NAME_LATITUDE, String.valueOf(latitude));
        args.putString(ARG_NAME_LONGITUDE, String.valueOf(longitude));
        if(!NetworkUtil.connectionPresent(context)){
            mRestaurantListCallback.onFailure(ErrorCodes.NO_NETWORK, context.getString(R.string.no_internet_connection));
            return;
        }
        new ApiCall().execute(args);
    }

    private static void returnRestaurantList(GenericApiResult result){
        if(result == null){
            mRestaurantListCallback.onFailure(ErrorCodes.SERVER_ERROR, "Server error");
            return;
        }
        Log.d("JOSH", "Class is "+result.getData().getClass());
        if(result.getData() == null || !result.getData().getClass().equals(ArrayList.class) || !TextUtils.isEmpty(result.getError())){
            Log.d("JOSH", "Error is: "+result.getError());
            Log.d("JOSH", "Data class is: "+result.getData().getClass());
            mRestaurantListCallback.onFailure(ErrorCodes.SERVER_ERROR, result.getError());
            return;
        }

        ArrayList<Restaurant> resData = (ArrayList<Restaurant>) result.getData();
        mRestaurantListCallback.onSuccess(resData);
    }

    // Synchronous api calls
    public static GenericApiResult getRestaurants(String longitude, String latitude){
        StringBuilder path = new StringBuilder(WEBSERVICE);
        path.append(GET_RESTAURANTS);
        path.append(latitude);
        path.append("/");
        path.append(longitude);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        JsonElement element = null;
        try {
            element = doRequest(path.toString(), nameValuePairs);
            Log.d("JOSH","Element: "+element);
            Type cType = new TypeToken<GenericApiResult<List<Restaurant>>>() {}.getType();
            return new Gson().fromJson(element, cType);
        } catch (JsonParseException e){
            Type cType = new TypeToken<GenericApiResult>() {}.getType();
            return new Gson().fromJson(element, cType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // API Access
    private static JsonElement doRequest(String url, List<NameValuePair> nameValuePairs) throws IOException {
        JsonElement element;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = GENARAL_CONNECTION_TIMEOUT;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = GENERAL_SOCKET_TIMEOUT;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        try {
            HttpGet httpGet = new HttpGet(url);
            String json;
            httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            httpGet.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httpGet);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), HTTP.UTF_8));
            json = reader.readLine();
            element = (json != null ? new JsonParser().parse(json) : null);
        } catch (MalformedJsonException e) {
            return null;
        }
        return element;
    }

    // Api access async task
    public static class ApiCall extends AsyncTask<Bundle, Void, GenericApiResult>{

        private int operationId;

        @Override
        public void onPreExecute(){
            super.onPreExecute();
            // stub
        }
        @Override
        protected GenericApiResult doInBackground(Bundle... params) {
            Bundle args = params[0];
            if(args == null) return null;
            operationId = params[0].getInt(OPERATION_ID);
            switch (operationId){
                case OPERATION_GET_RESTAURANTS:
                    String latitude = params[0].getString(ARG_NAME_LATITUDE);
                    String longitude = params[0].getString(ARG_NAME_LONGITUDE);
                    return getRestaurants(latitude, longitude);
            }
            return null;
        }
        @Override
        public void onPostExecute(GenericApiResult result){
            super.onPostExecute(result);
            switch (operationId){
                case OPERATION_GET_RESTAURANTS:
                    returnRestaurantList(result);
                    break;
            }
        }
    }
}
