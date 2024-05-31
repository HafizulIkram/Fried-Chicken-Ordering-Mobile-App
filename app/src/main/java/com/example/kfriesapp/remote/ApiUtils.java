package com.example.kfriesapp.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "https://whhaziq.000webhostapp.com/prestige/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static OrderService getOrderService() {
        return RetrofitClient.getClient(BASE_URL).create(OrderService.class);
    }

    public static MenuService getMenuService() {
        return RetrofitClient.getClient(BASE_URL).create(MenuService.class);
    }

    public static Menu_OrderService getOrdersMenuService() {
        return RetrofitClient.getClient(BASE_URL).create(Menu_OrderService.class);
    }


}