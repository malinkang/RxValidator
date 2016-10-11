package com.malinkang.rxvalidator;

import android.view.View;

public class ValidationFail {

    private View view;
    private int order;
    private String message;
    public ValidationFail(int order, View view, String message) {
        this.order = order;
        this.view = view;
        this.message = message;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}