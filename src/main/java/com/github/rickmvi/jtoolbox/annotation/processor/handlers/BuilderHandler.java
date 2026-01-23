package com.github.rickmvi.jtoolbox.annotation.processor.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.AnnotationProcessor;
import com.github.rickmvi.jtoolbox.annotation.processor.annotations.Builder;
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
import java.util.List;

/**
 * Handler for @Builder - generates builder pattern.
 */
public class BuilderHandler implements AnnotationHandler {
    private final AnnotationProcessor.ProcessingContext context;

    public BuilderHandler(AnnotationProcessor.ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void handle(Element element, RoundEnvironment roundEnv) throws IOException {
        if (element.getKind() != ElementKind.CLASS) {
            context.error(element, "@Builder can only be applied to classes");
            return;
        }

        TypeElement classElement = (TypeElement) element;
        Builder annotation = classElement.getAnnotation(Builder.class);
        ClassAnalyzer analyzer = new ClassAnalyzer(classElement, context);

        generateBuilder(classElement, annotation, analyzer.getAllFields());
    }

    private void generateBuilder(TypeElement classElement, Builder annotation,
                                 List<FieldInfo> fields) throws IOException {
        String className = classElement.getSimpleName().toString();
        String packageName = context.getElementUtils()
                .getPackageOf(classElement).getQualifiedName().toString();
        String builderClassName = className + annotation.builderClassName();

        JavaFileObject file = context.getFiler()
                .createSourceFile(packageName + "." + builderClassName);

        try (CodeWriter writer = new CodeWriter(new PrintWriter(file.openWriter()))) {
            writer.writePackage(packageName)
                    .writeGeneratedComment()
                    .writeLine();

            String accessModifier = annotation.access().toJavaModifier();
            writer.beginClass(accessModifier, builderClassName);

            // Builder fields
            for (FieldInfo field : fields) {
                writer.writeIndent()
                        .writeLine("private " + field.getTypeName() + " " + field.getName() + ";");
            }
            writer.writeLine();

            // Fluent setters
            for (FieldInfo field : fields) {
                writer.writeIndent()
                        .writeLine("public " + builderClassName + " " + field.getName() +
                                "(" + field.getTypeName() + " " + field.getName() + ") {");
                writer.writeIndent().writeIndent()
                        .writeLine("this." + field.getName() + " = " + field.getName() + ";");
                writer.writeIndent().writeIndent().writeLine("return this;");
                writer.writeIndent().writeLine("}");
                writer.writeLine();
            }

            // Build method
            writer.writeIndent()
                    .writeLine("public " + className + " " + annotation.buildMethodName() + "() {");
            writer.writeIndent().writeIndent().writeLine("try {");
            writer.writeIndent().writeIndent().writeIndent()
                    .writeLine(className + " instance = new " + className + "();");

            for (FieldInfo field : fields) {
                writer.writeIndent().writeIndent().writeIndent()
                        .writeLine("java.lang.reflect.Field f = " + className +
                                ".class.getDeclaredField(\"" + field.getName() + "\");");
                writer.writeIndent().writeIndent().writeIndent()
                        .writeLine("f.setAccessible(true);");
                writer.writeIndent().writeIndent().writeIndent()
                        .writeLine("f.set(instance, this." + field.getName() + ");");
            }

            writer.writeIndent().writeIndent().writeIndent().writeLine("return instance;");
            writer.writeIndent().writeIndent().writeLine("} catch (Exception e) {");
            writer.writeIndent().writeIndent().writeIndent()
                    .writeLine("throw new RuntimeException(\"Failed to build instance\", e);");
            writer.writeIndent().writeIndent().writeLine("}");
            writer.writeIndent().writeLine("}");

            writer.endClass();
        }

        // Generate static builder() method in separate file or note
        context.note(classElement,
                "Add this to your class: public static " + builderClassName +
                        " " + annotation.builderMethodName() + "() { return new " + builderClassName + "(); }");
    }
}