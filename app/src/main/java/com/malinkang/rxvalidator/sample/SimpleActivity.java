package com.malinkang.rxvalidator.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.malinkang.rxvalidator.RxValidator;
import com.malinkang.rxvalidator.ValidationFail;
import com.malinkang.rxvalidator.ValidationResult;
import com.malinkang.rxvalidator.annotations.MaxLength;
import com.malinkang.rxvalidator.annotations.MinLength;
import com.malinkang.rxvalidator.annotations.NotEmpty;
import com.malinkang.rxvalidator.annotations.RegExp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by malk on 16/9/27.
 */

public class SimpleActivity extends AppCompatActivity implements View.OnClickListener {

    @NotEmpty(order = 1, message = "FirstName不能为空")
    EditText firstName;
    TextInputLayout firstNameTextInputLayout;
    @NotEmpty(order = 2, message = "LastName不能为空")
    EditText lastName;
    TextInputLayout lastNameTextInputLayout;
    @NotEmpty(order = 3, message = "手机号不能为空")
    @RegExp(order = 4, message = "请输入正确的手机号", regexp = "^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}")
    EditText phone;
    TextInputLayout phoneTextInputLayout;
    @NotEmpty(order = 5, message = "密码不能为空")
    @MinLength(order = 6, length = 6, message = "密码长度不能小于6")
    @MaxLength(order = 7, length = 12, message = "密码长度不能大于12")
    EditText password;
    TextInputLayout passwordTextInputLayout;

    Button registerButton;

    Map<EditText, TextInputLayout> inputLayoutMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        firstName = (EditText) findViewById(R.id.et_first_name);
        firstNameTextInputLayout = (TextInputLayout) findViewById(R.id.first_name_text_input_layout);
        lastName = (EditText) findViewById(R.id.et_last_name);
        lastNameTextInputLayout = (TextInputLayout) findViewById(R.id.last_name_text_input_layout);
        phone = (EditText) findViewById(R.id.et_phone);
        phoneTextInputLayout = (TextInputLayout) findViewById(R.id.phone_text_input_layout);
        password = (EditText) findViewById(R.id.et_password);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.password_text_input_layout);
        registerButton = (Button) findViewById(R.id.btn_register);
        inputLayoutMap.put(firstName, firstNameTextInputLayout);
        inputLayoutMap.put(lastName, lastNameTextInputLayout);
        inputLayoutMap.put(phone, phoneTextInputLayout);
        inputLayoutMap.put(password, passwordTextInputLayout);
        registerButton.setOnClickListener(this);
    }


    Subscription subscription;
    private boolean isValid;

    @Override
    public void onClick(View v) {
        if (subscription == null || subscription.isUnsubscribed()) {
            subscription = RxValidator.validate(this).subscribe(new Action1<ValidationResult>() {
                @Override
                public void call(ValidationResult validationResult) {
                    isValid = validationResult.isValid;
                    for (EditText editText : inputLayoutMap.keySet()) {
                        TextInputLayout textInputLayout = inputLayoutMap.get(editText);
                        textInputLayout.setErrorEnabled(false);
                    }
                    if (!validationResult.isValid) {
                        ArrayList<ValidationFail> errors = validationResult.getFails();
                        for (ValidationFail fail : errors) {
                            TextInputLayout textInputLayout = inputLayoutMap.get(fail.getView());
                            textInputLayout.setErrorEnabled(true);
                            textInputLayout.setError(fail.getMessage());
                        }
                    }
                }
            });
        }
        if (isValid) {
            Toast.makeText(this, "Register Success...", Toast.LENGTH_SHORT).show();
        }

    }
}
