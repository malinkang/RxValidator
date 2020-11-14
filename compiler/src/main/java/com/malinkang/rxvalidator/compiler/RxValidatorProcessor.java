package com.malinkang.rxvalidator.compiler;

import com.google.auto.service.AutoService;
import com.malinkang.rxvalidator.annotations.MaxLength;
import com.malinkang.rxvalidator.annotations.MinLength;
import com.malinkang.rxvalidator.annotations.NotEmpty;
import com.malinkang.rxvalidator.annotations.RegExp;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RxValidatorProcessor extends AbstractProcessor {

    Filer filer;
    private Elements elements;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(NotEmpty.class.getCanonicalName());
        types.add(MaxLength.class.getCanonicalName());
        types.add(MinLength.class.getCanonicalName());
        types.add(RegExp.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Map<TypeElement, ValidationClass> classMap = new HashMap<>();

        for (Element element : roundEnvironment.getElementsAnnotatedWith(NotEmpty.class)) {
            parseNotEmpty(element, classMap);
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(MaxLength.class)) {
            parseMaxLength(element, classMap);
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(MinLength.class)) {
            parseMinLength(element, classMap);
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(RegExp.class)) {
            parseRegExp(element, classMap);
        }

        for (Map.Entry<TypeElement, ValidationClass> entry : classMap.entrySet()) {
            ValidationClass rxValidatorClass = entry.getValue();

            JavaFile javaFile = rxValidatorClass.brewJava();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
            }
        }

        return true;
    }

    //解析NotEmpty
    public void parseNotEmpty(Element element, Map<TypeElement, ValidationClass> validationClassMap) {
        //父Element
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        VariableElement variableElement = (VariableElement) element;
        NotEmpty notEmpty = element.getAnnotation(NotEmpty.class);
        int order = notEmpty.order();
        String message = notEmpty.message();
        boolean trim = notEmpty.trim();
        ValidationClass validationClass = validationClassMap.get(enclosingElement);
        if (validationClass == null) {
            validationClass = new ValidationClass(enclosingElement);
            validationClassMap.put(enclosingElement, validationClass);
        }
        ValidationField validationField = validationClass.getField(variableElement);
        Validation validation = new Validation(Validation.NOTEMPTY, order, message, validationField.getName());
        validation.addParam("trim", trim);
        validationField.addValidation(validation);

    }

    public void parseMaxLength(Element element, Map<TypeElement, ValidationClass> validationClassMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        VariableElement variableElement = (VariableElement) element;
        MaxLength maxLength = element.getAnnotation(MaxLength.class);
        int order = maxLength.order();
        String message = maxLength.message();
        int length = maxLength.length();
        ValidationClass validationClass = validationClassMap.get(enclosingElement);
        if (validationClass == null) {
            validationClass = new ValidationClass(enclosingElement);
            validationClassMap.put(enclosingElement, validationClass);
        }
        ValidationField validationField = validationClass.getField(variableElement);
        Validation validation = new Validation(Validation.MAXLENGTH, order, message, validationField.getName());
        validation.addParam("length", length);
        validationField.addValidation(validation);

    }

    public void parseMinLength(Element element, Map<TypeElement, ValidationClass> validationClassMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        VariableElement variableElement = (VariableElement) element;
        MinLength minLength = element.getAnnotation(MinLength.class);
        int order = minLength.order();
        String message = minLength.message();
        int length = minLength.length();
        ValidationClass validationClass = validationClassMap.get(enclosingElement);
        if (validationClass == null) {
            validationClass = new ValidationClass(enclosingElement);
            validationClassMap.put(enclosingElement, validationClass);
        }
        ValidationField validationField = validationClass.getField(variableElement);
        Validation validation = new Validation(Validation.MINLENGTH, order, message, validationField.getName());
        validation.addParam("length", length);
        validationField.addValidation(validation);

    }

    public void parseRegExp(Element element, Map<TypeElement, ValidationClass> validationClassMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        VariableElement variableElement = (VariableElement) element;
        RegExp regExp = element.getAnnotation(RegExp.class);
        int order = regExp.order();
        String message = regExp.message();
        String regexp = regExp.regexp();
        ValidationClass validationClass = validationClassMap.get(enclosingElement);
        if (validationClass == null) {
            validationClass = new ValidationClass(enclosingElement);
            validationClassMap.put(enclosingElement, validationClass);
        }
        ValidationField validationField = validationClass.getField(variableElement);
        Validation validation = new Validation(Validation.REGEXP, order, message, validationField.getName());
        validation.addParam("regexp", regexp);
        validationField.addValidation(validation);

    }
}
