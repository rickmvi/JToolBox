package com.github.rickmvi.jtoolbox.annotation.processor.handlers;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import java.io.IOException;

/**
 * Base interface for annotation handlers.
 */
public interface AnnotationHandler {
    void handle(Element element, RoundEnvironment roundEnv) throws IOException;
}