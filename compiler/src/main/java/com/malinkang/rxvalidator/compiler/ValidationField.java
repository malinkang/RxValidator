package com.malinkang.rxvalidator.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.VariableElement;

/**
 * Created by malk on 16/9/23.
 */

public class ValidationField {
    private String name;
    private List<Validation> validations;

    public ValidationField(VariableElement element) {
        name = element.getSimpleName().toString();
        validations=new ArrayList<>();
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addValidation(Validation validation){
        validations.add(validation);
    }
}
