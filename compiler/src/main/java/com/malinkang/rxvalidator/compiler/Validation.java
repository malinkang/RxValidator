package com.malinkang.rxvalidator.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by malk on 16/9/23.
 */

public class Validation {
    public final static ClassName NOTEMPTY = ClassName.get("com.malinkang.rxvalidator.validator", "NotEmptyValidator");
    public final static ClassName MINLENGTH = ClassName.get("com.malinkang.rxvalidator.validator", "MinLengthValidator");
    public final static ClassName MAXLENGTH = ClassName.get("com.malinkang.rxvalidator.validator", "MaxLengthValidator");
    public final static ClassName REGEXP = ClassName.get("com.malinkang.rxvalidator.validator", "RegExpValidator");

    private ClassName className;
    private int order;
    private String message;
    private String viewName;

    Map<String, Object> params;


    Validation(ClassName className, int order, String message, String viewName) {
        this.className = className;
        this.order = order;
        this.message = message;
        this.viewName = viewName;
        params = new HashMap<>();
    }

    public void addParam(String key, Object value) {
        params.put(key, value);
    }

    public CodeBlock getValidator() {
        if (NOTEMPTY.equals(className)) {
            return CodeBlock.of("new $T($L,target.$L,$S,$L)", NOTEMPTY, order, viewName,message, params.get("trim"));
        } else if (MINLENGTH.equals(className)) {
            return CodeBlock.of("new $T($L,target.$L,$S,$L)", MINLENGTH, order, viewName,message, params.get("length"));
        } else if (MAXLENGTH.equals(className)) {
            return CodeBlock.of("new $T($L,target.$L,$S,$L)", MAXLENGTH, order, viewName,message, params.get("length"));
        } else if (REGEXP.equals(className)) {
            return CodeBlock.of("new $T($L,target.$L,$S,$S)", REGEXP, order, viewName,message, params.get("regexp"));
        }
        return null;
    }

}
