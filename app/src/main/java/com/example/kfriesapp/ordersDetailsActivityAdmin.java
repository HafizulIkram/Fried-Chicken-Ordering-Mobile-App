package com.example.kfriesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kfriesapp.adapter.Menu_OrderAdapter;
import com.example.kfriesapp.model.Orders;
import com.example.kfriesapp.model.Menu_Order;
import com.example.kfriesapp.model.SharedPrefManager;
import com.example.kfriesapp.model.User;
import com.example.kfriesapp.remote.ApiUtils;
import com.example.kfriesapp.remote.OrderService;
import com.example.kfriesapp.remote.Menu_OrderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ordersDetailsActivityAdmin extends AppCompatActivity {

    Menu_OrderService menuOrderService;

    Orders orders;
    private Spinner spUpdate_Status;
    Context context;
    RecyclerView orderMenuList;
    Menu_OrderAdapter adapter;

    private int orderID;
    private String orderDate;

    private String userEmail;

    private Button btnUpdate;

    private String orderStatus;

    private String orderRemark;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_details_admin);
        context = this; // get current activity context

        

        // get reference to the RecyclerView orderMenuList
        orderMenuList = findViewById(R.id.orderMenuListAdmin);

        //register for context menu
        registerForContextMenu(orderMenuList);

        // get order id sent by orderMenuListActivity, -1 if not found
        Intent intent = getIntent();
        orderID = intent.getIntExtra("orderID", -1);
        orderDate = intent.getStringExtra("orderDate");
        orderStatus = intent.getStringExtra("orderStatus");
        userEmail = intent.getStringExtra("userEmail");
        orderRemark = intent.getStringExtra("orderRemark");

        // Assuming you have a list of order status options
        String[] orderStatusOptions = {"New", "Preparing", "Ready"}; // replace with your actual order status options

        spUpdate_Status = findViewById(R.id.spUpdateStatus);

        // Create an ArrayAdapter using the orderStatusOptions and a default spinner layout
        ArrayAdapter<String> orderStatusAdapter = new ArrayAdapter<>(this, R.layout.update_spinner, orderStatusOptions);

        // Specify the layout to use when the list of choices appears
        orderStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spUpdate_Status.setAdapter(orderStatusAdapter);

        // Find the position of the order status in the options list
        int position = getPositionForOrderStatus(orderStatus, orderStatusOptions);

        // Set the Spinner selection
        spUpdate_Status.setSelection(position);


        TextView tvOrderID = findViewById(R.id.tvOrderID);
        TextView tvOrderDate = findViewById(R.id.tvOrderDate);;
        TextView tvUserEmail = findViewById(R.id.tvUserEmail);

        // set values
        tvOrderID.setText(orderID + "");
        tvOrderDate.setText(orderDate + "");
        tvUserEmail.setText(userEmail + "");

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get menuOrder service instance
        menuOrderService = ApiUtils.getOrdersMenuService();

        Call<List<Menu_Order>> call = menuOrderService.getOrdersMenu(user.getToken(),  orderID);
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



                // Get list of menuOrders object from response
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

        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder(v);

            }
        });
    }

    // Method to get position based on order status string
    private int getPositionForOrderStatus(String orderStatusString, String[] orderStatusArray) {
        if (orderStatusString != null && orderStatusArray != null) {
            for (int i = 0; i < orderStatusArray.length; i++) {
                if (orderStatusString.equals(orderStatusArray[i])) {
                    return i;
                }
            }
        }
        return 0; // Default position if not found or if input is null
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

    private void setOrderStatusSpinner(String orderStatus) {
        // Get the array of order status values from resources
        String[] orderStatusArray = getResources().getStringArray(R.array.order_status);

        // Find the index of the orderStatus in the array
        int index = getIndexFromArray(orderStatusArray, orderStatus);

        // Set the selection in the Spinner
        spUpdate_Status.setSelection(index);
    }

    // Helper method to find the index of a value in an array
    private int getIndexFromArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return 0; // Default to the first item if not found
    }

    public void updateOrder(View view) {


        // Get the selected order status from the spinner
        String updatedOrderStatus = spUpdate_Status.getSelectedItem().toString();

        int id = orderID;
        orders = new Orders(orderID, orderDate, updatedOrderStatus, userEmail, orderRemark);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to update the order record to the REST API
        OrderService orderService = ApiUtils.getOrderService();
        Call<Orders> call = orderService.updateOrders(user.getToken(), orders);

        Context context = this;
        // execute
        call.enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // order updated successfully?
                Orders updatedOrders = response.body();
                if (updatedOrders != null) {
                    // display message
                    Toast.makeText(context,
                            updatedOrders.getOrderID() + " updated successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to OrderListAdminActivity
                    Intent intent = new Intent(context, OrderListAdmin.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Update Order failed.");
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
}