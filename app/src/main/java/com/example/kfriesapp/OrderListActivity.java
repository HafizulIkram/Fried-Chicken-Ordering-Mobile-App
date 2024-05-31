package com.example.kfriesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kfriesapp.adapter.OrderAdapter;
import com.example.kfriesapp.model.DeleteResponse;
import com.example.kfriesapp.model.Orders;
import com.example.kfriesapp.model.SharedPrefManager;
import com.example.kfriesapp.model.User;
import com.example.kfriesapp.remote.ApiUtils;
import com.example.kfriesapp.remote.OrderService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends AppCompatActivity {

    OrderService orderService;
    Context context;
    RecyclerView orderList;
    OrderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        context = this; // get current activity context

        // get reference to the RecyclerView orderList
        orderList = findViewById(R.id.orderList);

        //register for context menu
        registerForContextMenu(orderList);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get order service instance
        orderService = ApiUtils.getOrderService();

        // execute the call. send the user token when sending the query
        orderService.getAllOrders(user.getToken(), user.getEmail()).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // token is not valid/expired
                if (response.code() == 401) {
                    displayAlert("Session Invalid");
                }


                // Get list of order object from response
                List<Orders> orders = response.body();

                if(orders != null) {
                    // initialize adapter
                    adapter = new OrderAdapter(context, orders);

                    // set adapter to the RecyclerView
                    orderList.setAdapter(adapter);

                    // set layout to recycler view
                    orderList.setLayoutManager(new LinearLayoutManager(context));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orderList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    orderList.addItemDecoration(dividerItemDecoration);
                }
                else
                    Toast.makeText(context, "No data please insert new data", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to NewOrderActivity
                Intent intent = new Intent(getApplicationContext(), NewOrderActivity.class);

                Call<List<Orders>> call = orderService.getOrdersByStatus(user.getToken(), "Preparing");
                call.enqueue(new Callback<List<Orders>>() {
                    @Override
                    public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                        Log.d("MyApp:", "Response: " + response.raw().toString());

                        if(response.code() == 204){
                            startActivity(intent);
                        }

                        if (response.isSuccessful()) {
                            List<Orders> orders = response.body();

                            // Check if orders is null or empty
                            if (orders == null || orders.isEmpty()) {
                                // Handle the case where orders is null or empty
                                Log.e("MyApp:", "No orders received");
                                // Optionally, display a message to the user or take other actions
                                return; // Skip the rest of the logic
                            }

                            int orderCount = orders.size(); // Count the number of orders

                            if (orderCount < 10) {
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, "Restaurant is busy", Toast.LENGTH_LONG).show();
                            }
                            // Handle the orders as needed
                        } else {

                            if(response.code() == 204){
                                startActivity(intent);
                            }
                            // Handle error
                            Log.e("MyApp:", "Error: " + response.code());

                            // Optionally, display a message to the user or take other actions
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Orders>> call, Throwable t) {
                        // Handle failure
                        Log.e("MyApp:", "Failed to retrieve orders: " + t.getMessage());

                        // Optionally, display a message to the user or take other actions
                    }
                });
            }
        });



    }

    private void updateListView() {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get order service instance
        orderService = ApiUtils.getOrderService();

        // execute the call. send the user token when sending the query
        orderService.getAllOrders(user.getToken(), user.getEmail()).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // token is not valid/expired
                if (response.code() == 401) {
                    displayAlert("Session Invalid");
                }


                // Get list of order object from response
                List<Orders> orders = response.body();

                if (orders != null) {
                    // initialize adapter
                    adapter = new OrderAdapter(context, orders);

                    // set adapter to the RecyclerView
                    orderList.setAdapter(adapter);

                    // set layout to recycler view
                    orderList.setLayoutManager(new LinearLayoutManager(context));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orderList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    orderList.addItemDecoration(dividerItemDecoration);
                } else{
                    Toast.makeText(context, "No data please insert new data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });


    }



    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();
        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                // user clicked report bugs menu item
                // call method to display dialog box
                LogoutApp();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orders_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Orders selectedOrders = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedOrders.toString());

        if (item.getItemId() == R.id.menu_details) {
            doViewDetails(selectedOrders);
        }

        else if (item.getItemId() == R.id.menu_delete){
            doDeleteOrders(selectedOrders);

        }


        return super.onContextItemSelected(item);
    }
    private void doViewDetails(Orders selectedOrders) {
        Log.d("MyApp:", "viewing details "+ selectedOrders.toString());
        Intent intent = new Intent(context, ordersDetailActivity.class);
        intent.putExtra("orderID", selectedOrders.getOrderID());
        intent.putExtra("orderDate", selectedOrders.getOrderDate());
        intent.putExtra("orderStatus", selectedOrders.getOrderStatus());
        intent.putExtra("orderRemark", selectedOrders.getOrderRemark());

        if (selectedOrders.getUserEmail() instanceof LinkedTreeMap<?, ?>) {
            LinkedTreeMap<?, ?> orders = (LinkedTreeMap<?, ?>) selectedOrders.getUserEmail();
            Log.d("MyApp", "idMap: " + orders.toString());

            Object userEmail = orders.get("email");

            intent.putExtra("userEmail", userEmail.toString());

            // Now you have id1 and id2, you can use them as needed
        } else {
            // Handle other cases or throw an exception
            Log.e("MyApp", "Unsupported type for menuID: " + selectedOrders.getUserEmail().getClass().getSimpleName());
        }
        startActivity(intent);
    }

    private void doDeleteOrders(Orders selectedOrders) {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        if(selectedOrders.getOrderStatus().equalsIgnoreCase("New")) {
            // prepare REST API call
            OrderService orderService = ApiUtils.getOrderService();
            Call<DeleteResponse> call = orderService.deleteOrder(user.getToken(), selectedOrders.getOrderID());
            // execute the call
            call.enqueue(new Callback<DeleteResponse>() {
                @Override
                public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                    if (response.isSuccessful()) {
                        // Successful deletion
                        displayAlert("Order had been deleted");
                        updateListView();
                    } else {
                        // Log detailed error information
                        Log.e("MyApp:", "Error: " + response.code());
                        try {
                            Log.e("MyApp:", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Display appropriate error message to the user
                        displayAlert("Order failed to delete");
                    }
                }

                @Override
                public void onFailure(Call<DeleteResponse> call, Throwable t) {
                    displayAlert("Error [" + t.getMessage() + "]");
                    Log.e("MyApp:", t.getMessage());
                }
            });
        }
        else
        {
            // display message
            Toast.makeText(getApplicationContext(),
                    "Cannot cancel order",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void LogoutApp(){

        // clear the shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).logout();

        // display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // forward to LoginActivity
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}

