package com.joshliberty.veganfriendly.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.activeandroid.query.Select;
import com.joshliberty.veganfriendly.models.Restaurant;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by caligula on 28/06/14.
 * This file is part of VeganFriendly.
 */
public class FileDownloader {

    private Context mContext;
    private OkHttpClient okHttpClient = new OkHttpClient();

    public FileDownloader(Context context){
        mContext = context;
    }

    public void fetchMissingImages(){
        Log.d(FileDownloader.class.getSimpleName(), "in fetchMissingImages");
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                Log.d(FileDownloader.class.getSimpleName(), "in fetchMissingImages AsyncTask");
                List<Restaurant> restaurantList = new Select().from(Restaurant.class).execute();
                for(Restaurant rest : restaurantList){
                    if(!rest.isImage_fetched()){
                        try {
                            Set<String> files = new HashSet<String>(Arrays.asList(mContext.fileList()));
                            if(!files.contains(rest.getImage_name())){
                                String url = ApiService.API_SERVER + ApiService.STATIC_PATH + rest.getImage_name();
                                Log.d(FileDownloader.class.getSimpleName(), "Downloading file: " + url);
                                Request request = new Request.Builder().url(url).build();
                                Response response = okHttpClient.newCall(request).execute();
                                FileOutputStream image = mContext.openFileOutput(rest.getImage_name(), Context.MODE_PRIVATE);
                                image.write(response.body().bytes());
                                image.flush();
                                image.close();
                                rest.setImage_fetched(true);
                            }
                        } catch(IOException e) {
                            Log.d(FileDownloader.class.getSimpleName(), "Download error.", e);
                        }
                    }
                }
                return null;
            }
        }.execute();

    }
}
