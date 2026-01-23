package com.github.rickmvi.jtoolbox.annotation.processor.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.AnnotationProcessor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * Placeholder handlers for other annotations.
 */
public class SetterHandler implements AnnotationHandler {
    private final AnnotationProcessor.ProcessingContext context;
    public SetterHandler(AnnotationProcessor.ProcessingContext context) { this.context = context; }

    @Override
    public void handle(Element element, RoundEnvironment roundEnv) {
        context.note(element, "@Setter processing not yet implemented");
    }
}