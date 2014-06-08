package com.joshliberty.veganfriendly.api;

import com.joshliberty.veganfriendly.models.Restaurant;

import java.util.List;

/**
 * Created by caligula on 06/06/14.
 * This file is part of VeganFriendly.
 */
public interface RestaurantListCallback {
    public void onSuccess(List<Restaurant> restaurants);
    public void onFailure(int error, String description);
}
