package com.malinkang.rxvalidator.validator;

import android.view.View;

import com.malinkang.rxvalidator.ValidationFail;


public class MinLengthValidator extends Validator {

    private int minLength;

    public MinLengthValidator(int order, View view, String message, int minLength) {
        super(order,view, message);
        this.minLength = minLength;
    }


    @Override
    public ValidationFail validate(CharSequence content) {
        boolean isSuccess=minLength<=content.length();
        ValidationFail validationFail=null;
        if(!isSuccess){
            validationFail= new ValidationFail(order,view, message);
        }
        return validationFail;
    }
}