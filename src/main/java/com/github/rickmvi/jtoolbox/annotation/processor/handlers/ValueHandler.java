package com.github.rickmvi.jtoolbox.annotation.processor.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.AnnotationProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

public class ValueHandler implements AnnotationHandler {
    private final AnnotationProcessor.ProcessingContext context;
    public ValueHandler(AnnotationProcessor.ProcessingContext context) { this.context = context; }

    @Override
    public void handle(Element element, RoundEnvironment roundEnv) {
        context.note(element, "@Value processing not yet implemented");
    }
}