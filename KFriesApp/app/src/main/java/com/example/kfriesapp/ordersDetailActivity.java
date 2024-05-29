package com.example.kfriesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kfriesapp.adapter.Menu_OrderAdapter;
import com.example.kfriesapp.model.Menu_Order;
import com.example.kfriesapp.model.SharedPrefManager;
import com.example.kfriesapp.model.User;
import com.example.kfriesapp.remote.ApiUtils;
import com.example.kfriesapp.remote.Menu_OrderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ordersDetailActivity extends AppCompatActivity {

    Menu_OrderService menuOrderService;

    Context context;
    RecyclerView orderMenuList;
    Menu_OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_detail);
        context = this; // get current activity context

        // get reference to the RecyclerView orderMenuList
        orderMenuList = findViewById(R.id.orderMenuList);

        //register for context menu
        registerForContextMenu(orderMenuList);

        // get order id sent by orderMenuListActivity, -1 if not found
        Intent intent = getIntent();
        int orderID = intent.getIntExtra("orderID", -1);
        String orderDate = intent.getStringExtra("orderDate");
        String orderStatus = intent.getStringExtra("orderStatus");
        String userEmail = intent.getStringExtra("userEmail");
        String orderRemark = intent.getStringExtra("orderRemark");


        TextView tvOrderID = findViewById(R.id.tvOrderID);
        TextView tvOrderDate = findViewById(R.id.tvOrderDate);
        TextView tvOrderStatus = findViewById(R.id.tvOrderStatus);
        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        TextView tvOrderRemark = findViewById(R.id.tvOrderRemark);

        // set values
        tvOrderID.setText(orderID + "");
        tvOrderDate.setText(orderDate + "");
        tvOrderStatus.setText(orderStatus + "");
        tvUserEmail.setText(userEmail + "");
        tvOrderRemark.setText(orderRemark);


        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get menuOrder service instance
        menuOrderService = ApiUtils.getOrdersMenuService();

        Call <List<Menu_Order>> call = menuOrderService.getOrdersMenu(user.getToken(),  orderID);
        // execute the call. send the user token when sending the query
        call.enqueue(new Callback<List<Menu_Order>>() {
            @Override
            public void onResponse(Call<List<Menu_Order>> call, Response<List<Menu_Order>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // token is not valid/expired
                if (response.code() == 401) {
                    displayAlert("Session Invalid");
                }



                // Get list of order object from response
                List<Menu_Order> orders = response.body();

                if(orders != null) {
                    // initialize adapter
                    adapter = new Menu_OrderAdapter(context, orders);

                    // set adapter to the RecyclerView
                    orderMenuList.setAdapter(adapter);

                    // set layout to recycler view
                    orderMenuList.setLayoutManager(new LinearLayoutManager(context));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orderMenuList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    orderMenuList.addItemDecoration(dividerItemDecoration);
                }
                else
                    Toast.makeText(context, "No data please insert new data", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<Menu_Order>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}