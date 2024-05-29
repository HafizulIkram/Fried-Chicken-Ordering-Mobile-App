package com.example.kfriesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Cart implements Parcelable {

    private int menuOrderID;

    private int menuID;

    private int orderID;

    private int qty;
    private List<Menu> menuList;

    private Menu menu;

    private Orders orders;


    public int getMenuID() {
        return menuID;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    public int getOrderID() {
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

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Cart() {
    }

    public Cart(int menuOrderID, int orderID, int menuID, int qty) {
        this.menuOrderID = menuOrderID;
        this.orderID = orderID;
        this.menuID = menuID;
        this.qty = qty;
    }

    protected Cart(Parcel in) {
        menuOrderID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(menuOrderID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public int getMenuOrderID() {
        return menuOrderID;
    }

    public void setMenuOrderID(int menuOrderID) {
        this.menuOrderID = menuOrderID;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }
}
