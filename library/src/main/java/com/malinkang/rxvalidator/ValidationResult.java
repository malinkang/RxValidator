package com.malinkang.rxvalidator;

import java.util.ArrayList;

/**
 * Created by malk on 16/9/27.
 */

public class ValidationResult {
    public boolean isValid;
    ArrayList<ValidationFail> fails;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public ArrayList<ValidationFail> getFails() {
        return fails;
    }

    public void setErrors(ArrayList<ValidationFail> fails) {
        this.fails = fails;
    }
}
