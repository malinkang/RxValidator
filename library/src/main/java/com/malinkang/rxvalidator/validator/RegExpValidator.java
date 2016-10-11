package com.malinkang.rxvalidator.validator;

import android.view.View;

import com.malinkang.rxvalidator.ValidationFail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by malk on 16/10/8.
 */

public class RegExpValidator extends Validator {
   private  String regexp;
    public RegExpValidator(int order, View view, String message,String regexp) {
        super(order, view, message);
        this.regexp = regexp;
    }

    @Override
    public ValidationFail validate(CharSequence content) {
       Pattern pattern = Pattern.compile(regexp);
        final Matcher matcher = pattern.matcher(content);
        boolean isSuccess= matcher.matches();
        ValidationFail validationFail = null;
        if (!isSuccess) {
            validationFail = new ValidationFail(order, view, message);
        }
        return validationFail;
    }
}
