package com.nicolasrf.carpoolurp.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Nicolas on 26/03/2018.
 */

public interface IGoogleService {
    @GET
    Call<String> getAddressName(@Url String url);

    @GET
    Call<String> getLocationFromAddress(@Url String url);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination);
}
