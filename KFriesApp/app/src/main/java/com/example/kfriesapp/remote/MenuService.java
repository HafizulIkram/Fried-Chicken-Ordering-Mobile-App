package com.example.kfriesapp.remote;

import com.example.kfriesapp.model.Menu;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MenuService {

    @GET("api/Menu/")
    Call<List<Menu>> getMenu(@Header("api-key") String api_key);
    @GET("api/Menu/")
    Call<Menu> getMenuDetails(@Header("api-key") String api_key, @Query("menuID") Object menuID);
}
