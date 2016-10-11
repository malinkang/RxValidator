package com.malinkang.rxvalidator.validator;

import android.view.View;

import com.malinkang.rxvalidator.ValidationFail;

/**
 * Created by malk on 16/9/30.
 */

public abstract class Validator {
    protected View view;
    protected String message;
    protected int order;

    public Validator(int order,View view, String message) {
        this.order = order;
        this.view = view;
        this.message = message;
    }

    public abstract ValidationFail validate(CharSequence content);
}
