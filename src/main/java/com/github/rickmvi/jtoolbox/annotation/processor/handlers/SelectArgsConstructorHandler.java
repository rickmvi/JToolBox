package com.github.rickmvi.jtoolbox.annotation.processor.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import com.github.rickmvi.jtoolbox.annotation.processor.AnnotationProcessor;
import com.github.rickmvi.jtoolbox.annotation.processor.annotations.SelectArgsConstructor;
import com.github.rickmvi.jtoolbox.annotation.processor.codegen.ClassAnalyzer;
import com.github.rickmvi.jtoolbox.annotation.processor.codegen.CodeWriter;
import com.github.rickmvi.jtoolbox.annotation.processor.codegen.FieldInfo;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handler for @SelectArgsConstructor - generates factory methods.
 * DOES NOT extend the class, creates static factory methods instead.
 */
public class SelectArgsConstructorHandler implements AnnotationHandler {
    private final AnnotationProcessor.ProcessingContext context;

    public SelectArgsConstructorHandler(AnnotationProcessor.ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void handle(Element element, RoundEnvironment roundEnv) throws IOException {
        if (element.getKind() != ElementKind.CLASS) {
            context.error(element, "@SelectArgsConstructor can only be applied to classes");
            return;
        }

        TypeElement classElement = (TypeElement) element;
        SelectArgsConstructor annotation = classElement.getAnnotation(SelectArgsConstructor.class);

        // Validate and get fields
        ClassAnalyzer analyzer = new ClassAnalyzer(classElement, context);
        List<FieldInfo> selectedFields = validateAndSelectFields(analyzer, annotation.value());

        if (selectedFields.isEmpty()) {
            context.warning(element, "@SelectArgsConstructor has no valid fields");
            return;
        }

        // Generate factory class
        generateFactoryClass(classElement, annotation, selectedFields);
    }

    private List<FieldInfo> validateAndSelectFields(ClassAnalyzer analyzer, String[] fieldNames) {
        List<FieldInfo> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (String fieldName : fieldNames) {
            // Check duplicates
            if (seen.contains(fieldName)) {
                context.warning(analyzer.getClassElement(),
                        "Duplicate field '" + fieldName + "' in @SelectArgsConstructor");
                continue;
            }
            seen.add(fieldName);

            // Find field
            FieldInfo field = analyzer.findField(fieldName);
            if (field == null) {
                context.error(analyzer.getClassElement(),
                        "Field '" + fieldName + "' does not exist in class");
                continue;
            }

            // Check if field is final
            if (field.isFinal()) {
                context.warning(analyzer.getClassElement(),
                        "Field '" + fieldName + "' is final and cannot be set via constructor");
            }

            result.add(field);
        }

        return result;
    }

    private void generateFactoryClass(TypeElement classElement,
                                      SelectArgsConstructor annotation,
                                      List<FieldInfo> fields) throws IOException {
        String className = classElement.getSimpleName().toString();
        String packageName = context.getElementUtils()
                .getPackageOf(classElement).getQualifiedName().toString();
        String factoryClassName = className + "Factory";

        JavaFileObject file = context.getFiler()
                .createSourceFile(packageName + "." + factoryClassName);

        try (CodeWriter writer = new CodeWriter(new PrintWriter(file.openWriter()))) {
            writer.writePackage(packageName)
                    .writeGeneratedComment()
                    .writeLine();

            // Factory class
            writer.beginClass("public final", factoryClassName);

            // Private constructor
            writer.writeLine()
                    .writeIndent().writeLine("private " + factoryClassName + "() {")
                    .writeIndent().writeIndent().writeLine("throw new UnsupportedOperationException(\"Factory class\");")
                    .writeIndent().writeLine("}")
                    .writeLine();

            // Generate factory method
            AccessLevel access = annotation.access();
            String methodName = annotation.staticName().isEmpty() ? "create" : annotation.staticName();

            writer.writeIndent().write(access.toJavaModifier() + " static ")
                    .write(className + " " + methodName + "(");

            // Parameters
            for (int i = 0; i < fields.size(); i++) {
                FieldInfo field = fields.get(i);
                writer.write(field.getTypeName() + " " + field.getName());
                if (i < fields.size() - 1) {
                    writer.write(", ");
                }
            }
            writer.writeLine(") {");

            // Method body - create instance and set fields via reflection
            writer.writeIndent().writeIndent().writeLine("try {");
            writer.writeIndent().writeIndent().writeIndent()
                    .writeLine(className + " instance = new " + className + "();");

            for (FieldInfo field : fields) {
                writer.writeIndent().writeIndent().writeIndent()
                        .writeLine("java.lang.reflect.Field f_" + field.getName() +
                                " = " + className + ".class.getDeclaredField(\"" + field.getName() + "\");");
                writer.writeIndent().writeIndent().writeIndent()
                        .writeLine("f_" + field.getName() + ".setAccessible(true);");
                writer.writeIndent().writeIndent().writeIndent()
                        .writeLine("f_" + field.getName() + ".set(instance, " + field.getName() + ");");
            }

            writer.writeIndent().writeIndent().writeIndent().writeLine("return instance;");
            writer.writeIndent().writeIndent().writeLine("} catch (Exception e) {");
            writer.writeIndent().writeIndent().writeIndent()
                    .writeLine("throw new RuntimeException(\"Failed to create instance\", e);");
            writer.writeIndent().writeIndent().writeLine("}");
            writer.writeIndent().writeLine("}");

            writer.endClass();
        }
    }
}