package com.malinkang.rxvalidator.validator;

import android.view.View;

import com.malinkang.rxvalidator.ValidationFail;

public class MaxLengthValidator extends Validator {

    private int maxLength;

    public MaxLengthValidator(int order, View view, String message, int maxLength) {
        super(order, view, message);
        this.maxLength = maxLength;
    }

    @Override
    public ValidationFail validate(CharSequence content) {
        boolean isSuccess = maxLength >= content.length();
        ValidationFail validationFail = null;
        if (!isSuccess) {
            validationFail = new ValidationFail(order, view, message);
        }
        return validationFail;
    }
}