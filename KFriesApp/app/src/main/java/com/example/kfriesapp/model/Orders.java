package com.example.kfriesapp.model;

public class Orders {



    private int orderID;
    private String orderDate;
    private String orderStatus;

    private Object userEmail;

    private String orderRemark;

    public Orders(){

    }

    public Orders(int orderID, String orderDate, String orderStatus, Object userEmail, String orderRemark) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.userEmail = userEmail.toString();
        this.orderRemark=orderRemark;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Object getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(Object userEmail) {
        this.userEmail = userEmail;
    }

    public String getOrderRemark() {
        return orderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        this.orderRemark = orderRemark;
    }
}

