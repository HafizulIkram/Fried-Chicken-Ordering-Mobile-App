package com.example.kfriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kfriesapp.model.ErrorResponse;
import com.example.kfriesapp.model.SharedPrefManager;
import com.example.kfriesapp.model.User;
import com.example.kfriesapp.remote.ApiUtils;
import com.example.kfriesapp.remote.UserService;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername;
    EditText edtPassword;
    Button btnLogin;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if the user is already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish(); // Stop this LoginActivity

            // Check user roles
            String userRoles = SharedPrefManager.getInstance(this).getUser().getRole();

            // Start the appropriate activity based on the user's role
            if (userRoles.contains("admin")) {
                startActivity(new Intent(this, OrderListAdmin.class));
            } else {
                startActivity(new Intent(this, OrderListActivity.class));
            }

            return;
        }

        // Get references to form elements
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Get UserService instance
        userService = ApiUtils.getUserService();

        // Set onClick action to btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get username and password entered by the user
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                // Validate form, make sure it is not empty
                if (validateLogin(username, password)) {
                    // Do login
                    doLogin(username, password);
                }
            }
        });
    }

    /**
     * Validate value of username and password entered. Client side validation.
     *
     * @param username
     * @param password
     * @return
     */
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Call REST API to login
     *
     * @param username
     * @param password
     */
    private void doLogin(String username, String password) {
        Call<User> call;

        // Determine the type of login based on the presence of '@' in the username
        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // Received reply from REST API
                if (response.isSuccessful()) {
                    // Parse response to POJO
                    User user = response.body();
                    if (user.getToken() != null) {
                        // Successful login. Server replies a token value
                        displayToast("Login successful");
                        displayToast("Token: " + user.getToken());

                        // Store value in Shared Preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        finish();

                        // Start the appropriate activity based on the user's role
                        if (user.getRole().equalsIgnoreCase("admin")) {
                            startActivity(new Intent(getApplicationContext(), OrderListAdmin.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), OrderListActivity.class));
                        }
                    }
                } else if (response.errorBody() != null) {
                    // Parse response to POJO
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ErrorResponse errorResponse = new Gson().fromJson(errorResp, ErrorResponse.class);
                    displayToast(errorResponse.getError().getMessage());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
            }
        });
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
