package com.example.kfriesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kfriesapp.adapter.MenuAdapter;
import com.example.kfriesapp.model.Cart;
import com.example.kfriesapp.model.Menu;
import com.example.kfriesapp.model.Orders;
import com.example.kfriesapp.model.Menu_Order;
import com.example.kfriesapp.model.SharedPrefManager;
import com.example.kfriesapp.model.User;
import com.example.kfriesapp.remote.ApiUtils;
import com.example.kfriesapp.remote.MenuService;
import com.example.kfriesapp.remote.OrderService;
import com.example.kfriesapp.remote.Menu_OrderService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewOrderActivity extends AppCompatActivity  {

    MenuService menuService;
    Menu_OrderService menuorderService;
    OrderService orderService;
    Context context;
    RecyclerView orderMenu;

    EditText etOrderRemark;
    MenuAdapter adapter;

    Button btnCheckout;

    Button btnCalculate;


    private int userID;
    private List<Menu> menus;

    String orderRemark;



    private List <Menu> itemsInCart;



    private Cart menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        context = this; // get current activity context


        // Initialize menuList
        menuList = new Cart();

        orderMenu = findViewById(R.id.orderMenu);
        //register for context menu
        registerForContextMenu(orderMenu);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get menu service instance
        menuService = ApiUtils.getMenuService();

        // execute the call. send the user token when sending the query
        menuService.getMenu(user.getToken()).enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // token is not valid/expired
                if (response.code() == 401) {
                    displayAlert("Session Invalid");
                }

                // Get list of menu object from response
                menus = response.body();

                // initialize adapter
                adapter = new MenuAdapter(context, menus);

                // set adapter to the RecyclerView
                orderMenu.setAdapter(adapter);

                // set layout to recycler view
                orderMenu.setLayoutManager(new LinearLayoutManager(context));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orderMenu.getContext(),
                        DividerItemDecoration.VERTICAL);
                orderMenu.addItemDecoration(dividerItemDecoration);

                adapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {
                    @Override
                    public void onAddToCartClick(Menu menus) {
                        if(itemsInCart == null) {
                            itemsInCart = new ArrayList<>();
                        }
                        itemsInCart.add(menus);
                    }

                });

            }

            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });

        btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (itemsInCart != null && itemsInCart.size() <= 0) {
                    Toast.makeText(NewOrderActivity.this, "Please add some items in cart.", Toast.LENGTH_SHORT).show();
                    return;
                }

                etOrderRemark = findViewById(R.id.etOrderRemark);
                orderRemark = etOrderRemark.getText().toString();


                if(orderRemark.equals(null)){
                    orderRemark = "none";
                }

                menuList.setMenuList(itemsInCart);


                createOrder(menuList);

            }
        });

        btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your calculation logic here
                menuList.setMenuList(itemsInCart);
                calculateTotal(menuList);


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

    public void createOrder(Cart menuList){

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();


        Orders o = new Orders(0, LocalDateTime.now().format(dateFormatter).toString(), "New", user.getEmail(), orderRemark);

        if(menuList.getMenuList() != null) {

            // send request to add new order to the REST API
            orderService = ApiUtils.getOrderService();
            Call<Orders> call = orderService.addOrders(user.getToken(), o);

            call.enqueue(new Callback<Orders>() {
                @Override
                public void onResponse(Call<Orders> call, Response<Orders> response) {


                    // for debug purpose
                    Log.d("MyApp:", "Response: " + response.raw().toString());

                    // invalid session?
                    if (response.code() == 401)
                        displayAlert("Invalid session. Please re-login");

                    // Order added successfully?
                    Orders addedOrders = response.body();

                    if (addedOrders != null) {

                        addMenuOrder(menuList, addedOrders.getOrderID());

                        // display message
                        Toast.makeText(context,
                                addedOrders.getOrderID() + " added successfully.",
                                Toast.LENGTH_LONG).show();

                        // end this activity and forward user to OrderListActivity
                        Intent intent = new Intent(context, OrderListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        displayAlert("Add New Order failed.");
                    }
                }

                @Override
                public void onFailure(Call<Orders> call, Throwable t) {
                    displayAlert("Error [" + t.getMessage() + "]");
                    // for debug purpose
                    Log.d("MyApp:", "Error: " + t.getCause().getMessage());
                }
            });
        }else{
            // display message
            Toast.makeText(getApplicationContext(),
                    "Cart is empty",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void addMenuOrder(Cart menuList, int orderID) {


        for (Menu menu : menuList.getMenuList()) {

            Menu_Order menuOrder = new Menu_Order(0, orderID,  menu.getMenuID(), menu.getQuantity(), "Help");

            // get user info from SharedPreferences
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            int id =  orderID;
            int menuID =  menuList.getMenuOrderID();
            menuorderService = ApiUtils.getOrdersMenuService();
            Call<Menu_Order> call = menuorderService.addOrdersMenu(user.getToken(), 0, orderID, menu.getMenuID(),menu.getQuantity());

            // execute
            call.enqueue(new Callback<Menu_Order>() {
                @Override
                public void onResponse(Call<Menu_Order> call, Response<Menu_Order> response) {

                    // for debug purpose
                    Log.d("MyApp:", "Response: " + response.raw().toString());

                    // invalid session?
                    if (response.code() == 401)
                        displayAlert("Invalid session. Please re-login");


                    if (response.isSuccessful()) {
                        // Handle success
                        Menu_Order menuOrder = response.body();
                    } else {
                        if (response.code() == 422) {
                            try {
                                String errorResp = response.errorBody().string();
                                Log.e("MyApp", "Error 422: " + errorResp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Handle other error codes as needed
                            Log.e("MyApp", "Error: " + response.code());
                        }
                    }

                        // Handle error
                        Log.e("APIError", response.message());

                }

                @Override
                public void onFailure(Call<Menu_Order> call, Throwable t) {
                    displayAlert("Error [" + t.getMessage() + "]");
                    // for debug purpose
                    Log.d("MyApp:", "Error: " + t.getCause().getMessage());
                }
            });
        }
    }

    private void calculateTotal(Cart menuList) {

        double totalAmount = 0;
        if (menuList != null ) {

            // Get the item at the specified position
            for (Menu menu : menuList.getMenuList()) {
                // Implement your calculation logic here using the specific item
                totalAmount = totalAmount + (menu.getMenuPrice() * menu.getQuantity());

            }

            // Format the double value with two decimal places
            String formattedPrice = String.format("%.2f", totalAmount);
            TextView tvPrice = findViewById(R.id.tvPrice);
            tvPrice.setText("RM " + formattedPrice);
        } else {
            // Handle invalid position or null menuList
            Toast.makeText(NewOrderActivity.this, "Invalid position or menu list is null.", Toast.LENGTH_SHORT).show();
        }
    }


}

