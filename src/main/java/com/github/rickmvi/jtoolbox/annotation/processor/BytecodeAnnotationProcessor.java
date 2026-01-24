package com.github.rickmvi.jtoolbox.annotation.processor;

import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.handlers.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Annotation Processor que manipula bytecode diretamente usando ASM.
 * Similar ao Lombok, modifica as classes compiladas em vez de gerar código fonte.
 */
@SupportedAnnotationTypes({
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Getter",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Setter",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Data",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Builder",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.AllArgsConstructor",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.NoArgsConstructor",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.RequiredArgsConstructor",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.ToString",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.EqualsAndHashCode",
        "com.github.rickmvi.jtoolbox.annotation.processor.annotations.Value"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BytecodeAnnotationProcessor extends AbstractProcessor {

    private ProcessingContext context;
    private Map<String, BytecodeHandler> handlers;
    private Map<String, TypeElement> pendingClasses;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.context = new ProcessingContext(
                processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(),
                processingEnv.getFiler(),
                processingEnv.getMessager()
        );

        this.pendingClasses = new HashMap<>();
        this.handlers = new HashMap<>();

        registerHandlers();
    }

    private void registerHandlers() {
        handlers.put("Getter", new GetterBytecodeHandler(context));
        handlers.put("Setter", new SetterBytecodeHandler(context));
        handlers.put("Data", new DataBytecodeHandler(context));
        handlers.put("ToString", new ToStringBytecodeHandler(context));
        handlers.put("EqualsAndHashCode", new EqualsAndHashCodeBytecodeHandler(context));
        handlers.put("AllArgsConstructor", new AllArgsConstructorBytecodeHandler(context));
        handlers.put("NoArgsConstructor", new NoArgsConstructorBytecodeHandler(context));
        handlers.put("RequiredArgsConstructor", new RequiredArgsConstructorBytecodeHandler(context));
        handlers.put("Builder", new BuilderBytecodeHandler(context));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processAllPendingClasses();
            return false;
        }

        // Coleta todas as classes anotadas
        for (TypeElement annotation : annotations) {
            String simpleName = annotation.getSimpleName().toString();

            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind() == ElementKind.CLASS) {
                    TypeElement classElement = (TypeElement) element;
                    String className = classElement.getQualifiedName().toString();
                    pendingClasses.put(className, classElement);
                }
            }
        }

        return true;
    }

    private void processAllPendingClasses() {
        for (Map.Entry<String, TypeElement> entry : pendingClasses.entrySet()) {
            try {
                transformClass(entry.getValue());
            } catch (Exception e) {
                context.error(entry.getValue(),
                        "Failed to transform bytecode: " + e.getMessage());
                if (context.isVerbose()) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void transformClass(TypeElement classElement) throws IOException {
        String className = classElement.getQualifiedName().toString();
        String internalName = className.replace('.', '/');

        context.info("Transforming class: " + className);

        // Lê o bytecode original
        byte[] originalBytecode = readClassBytecode(classElement);
        if (originalBytecode == null) {
            context.warning(classElement, "Could not read bytecode for transformation");
            return;
        }

        // Aplica transformações
        ClassReader reader = new ClassReader(originalBytecode);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        BytecodeTransformer transformer = new BytecodeTransformer(
                writer, classElement, context, handlers
        );

        reader.accept(transformer, ClassReader.EXPAND_FRAMES);

        byte[] transformedBytecode = writer.toByteArray();

        // Escreve o bytecode modificado
        writeBytecode(internalName, transformedBytecode);

        context.note(classElement, "Bytecode transformation completed");
    }

    private byte[] readClassBytecode(TypeElement classElement) throws IOException {
        String className = classElement.getQualifiedName().toString();
        String classFileName = className.replace('.', '/') + ".class";

        try {
            FileObject resource = context.getFiler().getResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    classFileName
            );

            try (InputStream is = resource.openInputStream()) {
                return is.readAllBytes();
            }
        } catch (IOException e) {
            // Arquivo pode não existir ainda na primeira compilação
            return null;
        }
    }

    private void writeBytecode(String internalName, byte[] bytecode) throws IOException {
        FileObject classFile = context.getFiler().createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                internalName + ".class"
        );

        try (OutputStream os = classFile.openOutputStream()) {
            os.write(bytecode);
        }
    }

    /**
     * Processing context compartilhado entre handlers
     */
    public static class ProcessingContext extends AnnotationProcessor.ProcessingContext {
        public ProcessingContext(
                javax.lang.model.util.Types typeUtils,
                javax.lang.model.util.Elements elementUtils,
                Filer filer,
                Messager messager
        ) {
            super(typeUtils, elementUtils, filer, messager);
        }
    }
}