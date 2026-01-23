package com.github.rickmvi.jtoolbox.annotation.processor.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.AnnotationProcessor;
import com.github.rickmvi.jtoolbox.annotation.processor.codegen.ClassAnalyzer;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;

/**
 * Handler for @Getter - generates getter methods.
 */
public class GetterHandler implements AnnotationHandler {
    private final AnnotationProcessor.ProcessingContext context;

    public GetterHandler(AnnotationProcessor.ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void handle(Element element, RoundEnvironment roundEnv) throws IOException {
        if (element.getKind() == ElementKind.CLASS) {
            handleClass((TypeElement) element);
        } else if (element.getKind() == ElementKind.FIELD) {
            handleField((VariableElement) element);
        }
    }

    private void handleClass(TypeElement classElement) {
        ClassAnalyzer analyzer = new ClassAnalyzer(classElement, context);
        context.note(classElement,
                "Generate getters for all fields. Add methods to your class or use IDE generation.");
    }

    private void handleField(VariableElement fieldElement) {
        context.note(fieldElement,
                "Generate getter for field: " + fieldElement.getSimpleName());
    }
}