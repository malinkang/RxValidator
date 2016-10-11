package com.malinkang.rxvalidator.validator;

import android.text.TextUtils;
import android.view.View;

import com.malinkang.rxvalidator.ValidationFail;

/**
 * Created by malk on 16/9/30.
 */

public class NotEmptyValidator extends Validator {
    private boolean trim;

    public NotEmptyValidator(int order, View view, String message, boolean trim) {
        super(order, view, message);
        this.trim = trim;
    }

    @Override
    public ValidationFail validate(CharSequence content) {
        boolean isSuccess;
        if (trim) {
            isSuccess = !TextUtils.isEmpty(content.toString().trim());
        } else {
            isSuccess = !TextUtils.isEmpty(content.toString());
        }
        ValidationFail validationFail = null;
        if (!isSuccess) {
            validationFail = new ValidationFail(order, view, message);
        }
        return validationFail;
    }
}
