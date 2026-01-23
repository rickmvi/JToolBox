package com.github.rickmvi.jtoolbox.annotation.processor.codegen;

import com.github.rickmvi.jtoolbox.annotation.processor.AnnotationProcessor.ProcessingContext;
import com.github.rickmvi.jtoolbox.annotation.processor.annotations.NonNull;
import lombok.Getter;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analyzes a class and provides information about its structure.
 * Handles inheritance, field filtering, and validation.
 */
public class ClassAnalyzer {
    @Getter
    private final TypeElement            classElement;
    private final ProcessingContext      context;
    private final Map<String, FieldInfo> fieldMap;
    private final List<FieldInfo>        allFields;

    public ClassAnalyzer(TypeElement classElement, ProcessingContext context) {
        this.classElement = classElement;
        this.context      = context;
        this.allFields    = new ArrayList<>();
        this.fieldMap     = new HashMap<>();

        analyzeClass();
    }

    private void analyzeClass() {
        collectFields(classElement);
    }

    private void collectFields(TypeElement element) {
        // Collect fields from current class
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement fieldElement = (VariableElement) enclosedElement;

                // Skip static fields
                if (fieldElement.getModifiers().contains(Modifier.STATIC)) {
                    continue;
                }

                FieldInfo fieldInfo = new FieldInfo(fieldElement);
                allFields.add(fieldInfo);
                fieldMap.put(fieldInfo.getName(), fieldInfo);
            }
        }

        // Collect from superclass
        TypeMirror superclass = element.getSuperclass();
        if (superclass.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) superclass;
            Element superElement = declaredType.asElement();

            if (superElement instanceof TypeElement) {
                TypeElement superTypeElement = (TypeElement) superElement;
                String superName = superTypeElement.getQualifiedName().toString();

                // Don't process Object
                if (!superName.equals("java.lang.Object")) {
                    collectFields(superTypeElement);
                }
            }
        }
    }

    public List<FieldInfo> getAllFields() {
        return new ArrayList<>(allFields);
    }

    public List<FieldInfo> getNonFinalFields() {
        return allFields.stream()
                .filter(f -> !f.isFinal())
                .collect(Collectors.toList());
    }

    public List<FieldInfo> getRequiredFields() {
        return allFields.stream()
                .filter(f -> f.isFinal() || f.hasAnnotation(NonNull.class))
                .collect(Collectors.toList());
    }

    public FieldInfo findField(String name) {
        return fieldMap.get(name);
    }

    public boolean hasField(String name) {
        return fieldMap.containsKey(name);
    }

    public int getFieldCount() {
        return allFields.size();
    }

    public String getClassName() {
        return classElement.getSimpleName().toString();
    }

    public String getQualifiedClassName() {
        return classElement.getQualifiedName().toString();
    }

    public String getPackageName() {
        return context.getElementUtils()
                .getPackageOf(classElement)
                .getQualifiedName()
                .toString();
    }

    public boolean hasDefaultConstructor() {
        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructor = (ExecutableElement) element;
                if (constructor.getParameters().isEmpty() &&
                        constructor.getModifiers().contains(Modifier.PUBLIC)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<FieldInfo> filterFields(String[] includeOnly, String[] exclude) {
        Set<String> includeSet = includeOnly != null && includeOnly.length > 0
                ? new HashSet<>(Arrays.asList(includeOnly))
                : null;

        Set<String> excludeSet = exclude != null
                ? new HashSet<>(Arrays.asList(exclude))
                : Collections.emptySet();

        return allFields.stream()
                .filter(f -> (includeSet == null || includeSet.contains(f.getName())) &&
                        !excludeSet.contains(f.getName()))
                .collect(Collectors.toList());
    }
}