package com.example.kfriesapp.remote;

import com.example.kfriesapp.model.DeleteResponse;
import com.example.kfriesapp.model.Orders;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface OrderService {

    @GET("api/Orders/?order=orderDate&orderType=desc")
    Call<List<Orders>> getAllOrders(@Header("api-key") String api_key,@Query("userEmail") String userEmail);

    @GET("api/Orders/?order=orderDate&orderType=desc")
    Call<List<Orders>> getAllOrdersAdmin(@Header("api-key") String api_key);

    @POST("api/Orders/")
    Call<Orders> addOrders(@Header ("api-key") String apiKey, @Body Orders orders);

    @POST("api/Orders/delete/{orderID}")
    Call<DeleteResponse> deleteOrder(@Header("api-key") String apiKey, @Path("orderID") int id);


    @POST("api/Orders/update")
    Call<Orders> updateOrders(@Header ("api-key") String apiKey, @Body Orders orders);

    @GET("api/Orders/")
    Call<List<Orders>> getOrdersByStatus(@Header("api-key") String apiKey,  @Query("orderStatus") String orderStatusList);


}
