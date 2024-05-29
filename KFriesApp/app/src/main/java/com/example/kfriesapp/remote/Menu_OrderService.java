package com.example.kfriesapp.remote;

import com.example.kfriesapp.model.Menu_Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Menu_OrderService {

    @GET("api/Menu_Order/")
    Call<List<Menu_Order>> getOrdersMenu(@Header("api-key") String api_key, @Query("orderID") int orderID);
    @FormUrlEncoded
    @POST("api/Menu_Order/")
    Call<Menu_Order> addOrdersMenu(@Header("api-key") String apiKey, @Field("OrderMenuID") int menuOrderID,
                                   @Field("orderID") int orderID,
                                   @Field("menuID") int menuID,
                                   @Field("qty") int qty);


}
