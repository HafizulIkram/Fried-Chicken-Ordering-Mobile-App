package com.example.kfriesapp.model;

public class Menu_Order {

    private int menuOrderID;
    private Object orderID;
    private Object menuID;
    private int qty;

    private Menu menu;
    private String test;


    public Menu_Order(int menuOrderID, Object orderID, Object menuID, int qty, String test) {
        this.menuOrderID = menuOrderID;
        this.orderID = orderID;
        this.menuID = menuID;
        this.qty = qty;
        this.test = test;
    }

    public int getMenuOrderID() {
        return menuOrderID;
    }

    public void setMenuOrderID(int menuOrderID) {
        this.menuOrderID = menuOrderID;
    }

    public Object getMenuID() {
        return menuID;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    public Object getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }






}
