package com.github.rickmvi.jtoolbox.annotation.processor;

import com.github.rickmvi.jtoolbox.annotation.processor.handlers.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * The orchestrator of the JToolbox compile-time transformation logic.
 * <p>This processor implements the standard Pluggable Annotation Processing API (JSR 269).
 * Instead of containing specific generation logic, it maintains a registry of
 * {@link AnnotationHandler} implementations, providing a clean separation of concerns.</p>
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 * <li><strong>Environment Setup:</strong> Initializes the {@link ProcessingContext} with
 * compiler utilities (Types, Elements, Filer).</li>
 * <li><strong>Handler Registration:</strong> Maps annotation simple names to their
 * respective logic handlers.</li>
 * <li><strong>Round Management:</strong> Iterates through found annotations in each
 * compilation round and triggers the appropriate handlers.</li>
 * <li><strong>Error Reporting:</strong> Provides a centralized way to report compiler
 * errors and warnings without breaking the whole process unless necessary.</li>
 * </ul>
 *
 *
 *
 * @author Rick M. Viana
 * @version 2.0
 */
@SupportedAnnotationTypes({
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.SelectArgsConstructor",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.AllArgsConstructor",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.NoArgsConstructor",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.RequiredArgsConstructor",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Builder",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Getter",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Setter",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Data",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Value",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.ToString",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.EqualsAndHashCode"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AnnotationProcessor extends AbstractProcessor {

    private ProcessingContext context;
    private Map<String, AnnotationHandler> handlers;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.context = new ProcessingContext(
                processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(),
                processingEnv.getFiler(),
                processingEnv.getMessager()
        );

        // Register handlers
        this.handlers = new HashMap<>();
        registerHandler("SelectArgsConstructor",   new SelectArgsConstructorHandler  (context));
        registerHandler("AllArgsConstructor",      new AllArgsConstructorHandler     (context));
        registerHandler("NoArgsConstructor",       new NoArgsConstructorHandler      (context));
        registerHandler("RequiredArgsConstructor", new RequiredArgsConstructorHandler(context));
        registerHandler("Builder",                 new BuilderHandler                (context));
        registerHandler("Getter",                  new GetterHandler                 (context));
        registerHandler("Setter",                  new SetterHandler                 (context));
        registerHandler("Data",                    new DataHandler                   (context));
        registerHandler("Value",                   new ValueHandler                  (context));
        registerHandler("ToString",                new ToStringHandler               (context));
        registerHandler("EqualsAndHashCode",       new EqualsAndHashCodeHandler      (context));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
        // Don't process in the final round
        if (roundEnv.processingOver()) {
            return false;
        }

        // Process only detected annotations
        for (TypeElement annotation : annotations) {
            String simpleName = annotation.getSimpleName().toString();
            AnnotationHandler handler = handlers.get(simpleName);

            if (handler != null) {
                for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                    try {
                        handler.handle(element, roundEnv);
                    } catch (Exception e) {
                        context.error(element, "Failed to process @" + simpleName + ": " + e.getMessage());
                        if (context.isVerbose()) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return true;
    }

    private void registerHandler(String annotationName, AnnotationHandler handler) {
        handlers.put(annotationName, handler);
    }

    /**
     * Processing context shared across handlers.
     */
    @Getter
    public static class ProcessingContext {
        private final Types typeUtils;
        private final Elements elementUtils;
        private final Filer filer;
        private final Messager messager;
        private final boolean verbose;

        public ProcessingContext(Types typeUtils, Elements elementUtils,
                                 Filer filer, Messager messager) {
            this.typeUtils = typeUtils;
            this.elementUtils = elementUtils;
            this.filer = filer;
            this.messager = messager;
            this.verbose = Boolean.getBoolean("annotation.processor.verbose");
        }

        public void error(Element element, String message) {
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
        }

        public void warning(Element element, String message) {
            messager.printMessage(Diagnostic.Kind.WARNING, message, element);
        }

        public void note(Element element, String message) {
            messager.printMessage(Diagnostic.Kind.NOTE, message, element);
        }

        public void info(String message) {
            messager.printMessage(Diagnostic.Kind.NOTE, message);
        }
    }
}