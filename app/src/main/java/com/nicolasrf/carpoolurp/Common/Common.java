package com.nicolasrf.carpoolurp.Common;

import com.nicolasrf.carpoolurp.model.User;
import com.nicolasrf.carpoolurp.remote.GoogleRetrofitClient;
import com.nicolasrf.carpoolurp.remote.IGoogleService;

/**
 * Created by Nicolas on 5/06/2018.
 */

public class Common {

    public static User currentUser = new User();

    /* for Google API service**/
    public static final String googleAPIUrl = "https://maps.googleapis.com/";
    public static IGoogleService getGoogleMapsAPI(){
        return GoogleRetrofitClient.getGoogleClient(googleAPIUrl).create(IGoogleService.class);
    }
    /**/
}
