package com.github.rickmvi.jtoolbox.annotation.processor.codegen;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class FieldInfo {
    private final VariableElement element;
    private final String name;
    private final TypeMirror type;
    private final boolean isFinal;
    private final boolean isStatic;
    private final boolean isPrivate;

    public FieldInfo(VariableElement element) {
        this.element = element;
        this.name = element.getSimpleName().toString();
        this.type = element.asType();
        this.isFinal = element.getModifiers().contains(Modifier.FINAL);
        this.isStatic = element.getModifiers().contains(Modifier.STATIC);
        this.isPrivate = element.getModifiers().contains(Modifier.PRIVATE);
    }

    public VariableElement getElement() { return element; }
    public String getName() { return name; }
    public TypeMirror getType() { return type; }
    public String getTypeName() { return type.toString(); }
    public boolean isFinal() { return isFinal; }
    public boolean isStatic() { return isStatic; }
    public boolean isPrivate() { return isPrivate; }

    public boolean hasAnnotation(Class<?> annotationClass) {
        return element.getAnnotation((Class) annotationClass) != null;
    }

    @Override
    public String toString() {
        return "FieldInfo{name='" + name + "', type=" + type + ", final=" + isFinal + "}";
    }
}