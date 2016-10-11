package com.malinkang.rxvalidator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.Observable;

/**
 * Created by malk on 16/9/27.
 */

public final class RxValidator {
    public static Observable<ValidationResult> validate(Object target){
        try {
            Class<?> validatorClass = Class.forName(target.getClass().getName() + "$$Validator");
            Method method =validatorClass.getMethod("validate",target.getClass());
            return (Observable<ValidationResult>) method.invoke(null,target);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
