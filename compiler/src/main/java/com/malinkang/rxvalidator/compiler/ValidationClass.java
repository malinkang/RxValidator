package com.malinkang.rxvalidator.compiler;

import com.malinkang.rxvalidator.ValidationFail;
import com.malinkang.rxvalidator.ValidationResult;
import com.malinkang.rxvalidator.validator.Validator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;


/**
 * Created by malk on 16/9/23.
 */

public class ValidationClass {

    static final ClassName RX_TEXTVIEW = ClassName.get("com.jakewharton.rxbinding.widget", "RxTextView");
    static final ClassName ANDROIDSCHEDULERS = ClassName.get("rx.android.schedulers", "AndroidSchedulers");
    static final ClassName OBSERVABLE = ClassName.get("rx", "Observable");
    static final ClassName FUNC1 = ClassName.get("rx.functions", "Func1");
    static final ClassName FUNCN = ClassName.get("rx.functions", "FuncN");

    private TypeName typeName;
    private ClassName validatorClassName;
    private Map<VariableElement, ValidationField> fieldMap; //存储字段

    public ValidationClass(TypeElement enclosingElement) {
        typeName = TypeName.get(enclosingElement.asType());
        String packageName = getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        validatorClassName = ClassName.get(packageName, className + "$$Validator");
        fieldMap = new HashMap<>();
    }

    public ValidationField getField(VariableElement element) {
        ValidationField field = fieldMap.get(element);
        if (field == null) {
            field = new ValidationField(element);
            fieldMap.put(element, field);
        }
        return field;
    }


    JavaFile brewJava() {
        return JavaFile.builder(validatorClassName.packageName(), createType())
                .addFileComment("Generated code from RxValidator. Do not modify!")
                .build();
    }

    private TypeSpec createType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(validatorClassName.simpleName())
                .addModifiers(PUBLIC);
        result.addMethod(createMethod());
        return result.build();
    }

    private MethodSpec createMethod() {
        MethodSpec.Builder validator = MethodSpec.methodBuilder("validate")
                .addModifiers(PUBLIC, STATIC)
                .returns(ParameterizedTypeName.get(OBSERVABLE, TypeName.get(ValidationResult.class)));
        validator.addParameter(typeName, "target");


        FieldSpec validatorField = FieldSpec.builder(Validator.class, "validator")
                .addModifiers(PRIVATE)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(Validator.class, "validator")
                .addStatement("this.$N = $N", "validator", "validator")
                .build();

        MethodSpec call = MethodSpec.methodBuilder("call")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CharSequence.class, "charSequence")
                .addStatement("return $T.just($N.validate(charSequence))",OBSERVABLE, "validator")
                .returns(ParameterizedTypeName.get(OBSERVABLE, TypeName.get(ValidationFail.class)))
                .build();
        TypeSpec.Builder validatorFunc1 = TypeSpec.classBuilder("ValidatorFunc1")
                .addSuperinterface(ParameterizedTypeName.get(FUNC1, TypeName.get(CharSequence.class),
                        ParameterizedTypeName.get(OBSERVABLE, TypeName.get(ValidationFail.class))))
                .addField(validatorField)
                .addMethod(constructor)
                .addMethod(call);
        validator.addStatement("$L",validatorFunc1.build());

        validator.addStatement("$T<$T<$T>> observables = new $T<>()", List.class, OBSERVABLE, ValidationFail.class, ArrayList.class);
        validator.addStatement("$T<$T> validationFailObservable", OBSERVABLE, ValidationFail.class);
        for (Map.Entry<VariableElement, ValidationField> entry : fieldMap.entrySet()) {
            ValidationField field = entry.getValue();
            validator.addStatement("$T<$T> $LObservable = $T.textChanges(target.$L)", OBSERVABLE, CharSequence.class, field.getName(), RX_TEXTVIEW, field.getName());
            List<Validation> validations = field.getValidations();
            for (Validation validation : validations) {
                validator.addStatement("validationFailObservable=$LObservable.flatMap(new ValidatorFunc1($L))", field.getName(), validation.getValidator());
                validator.addStatement("observables.add(validationFailObservable)");
            }
        }
        TypeSpec.Builder func = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(FUNCN, TypeName.get(ValidationResult.class)));

        TypeSpec.Builder compare = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(Comparator.class, ValidationFail.class));

        MethodSpec.Builder compareMethod = MethodSpec.methodBuilder("compare")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addParameter(ValidationFail.class, "lhs")
                .addParameter(ValidationFail.class, "rhs")
                .addStatement("return lhs.getOrder() < rhs.getOrder() ? -1 : (lhs.getOrder() == rhs.getOrder() ? 0 : 1)");
        compare.addMethod(compareMethod.build());

        MethodSpec.Builder funcMethod = MethodSpec.methodBuilder("call")
                .addAnnotation(Override.class)
                .addParameter(Object[].class, "args")
                .varargs()
                .addModifiers(PUBLIC)
                .returns(ValidationResult.class)
                .addStatement("$T isValid=$L", boolean.class, true)
                .addStatement("$T validationResult=new $T()", ValidationResult.class, ValidationResult.class)
                .addStatement("$T<$T> errors=new $T<$T>()", ArrayList.class, ValidationFail.class, ArrayList.class, ValidationFail.class)
                .beginControlFlow(" for ($T arg : args) ", Object.class)
                .beginControlFlow("if(arg !=null)")
                .addStatement("$T error = ($T) arg", ValidationFail.class, ValidationFail.class)
                .addStatement("isValid = $L", false)
                .addStatement("errors.add(error)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("$T.sort(errors,$L)", Collections.class, compare.build())
                .addStatement("validationResult.isValid=isValid")
                .addStatement("validationResult.setErrors(errors)")
                .addStatement("return validationResult");
        func.addMethod(funcMethod.build());
        validator.addStatement("return $T.combineLatest(observables,$L)" +
                ".observeOn($T.mainThread())" +
                ".subscribeOn($T.mainThread())", OBSERVABLE, func.build(), ANDROIDSCHEDULERS, ANDROIDSCHEDULERS);
        return validator.build();
    }


}
