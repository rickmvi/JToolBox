package com.github.rickmvi.jtoolbox.annotation.processor.bytecode;

import com.github.rickmvi.jtoolbox.annotation.processor.BytecodeAnnotationProcessor.ProcessingContext;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.handlers.BytecodeHandler;
import org.objectweb.asm.*;

import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * Visitor ASM que coordena as transformações de bytecode.
 */
public class BytecodeTransformer extends ClassVisitor {

    private final TypeElement classElement;
    private final ProcessingContext context;
    private final Map<String, BytecodeHandler> handlers;
    private String className;
    private ClassInfo classInfo;

    public BytecodeTransformer(
            ClassWriter writer,
            TypeElement classElement,
            ProcessingContext context,
            Map<String, BytecodeHandler> handlers
    ) {
        super(Opcodes.ASM9, writer);
        this.classElement = classElement;
        this.context = context;
        this.handlers = handlers;
    }

    @Override
    public void visit(
            int version, int access, String name, String signature,
            String superName, String[] interfaces
    ) {
        this.className = name;
        this.classInfo = new ClassInfo(name, superName, access);

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(
            int access, String name, String descriptor,
            String signature, Object value
    ) {
        // Registra informações do field
        classInfo.addField(name, descriptor, access);

        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor,
            String signature, String[] exceptions
    ) {
        // Registra métodos existentes
        classInfo.addMethod(name, descriptor, access);

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        // Aplica todos os handlers após processar a classe
        for (Map.Entry<String, BytecodeHandler> entry : handlers.entrySet()) {
            String annotationName = entry.getKey();
            BytecodeHandler handler = entry.getValue();

            if (hasAnnotation(annotationName)) {
                try {
                    handler.transform(cv, classElement, classInfo);
                } catch (Exception e) {
                    context.error(classElement,
                            "Error in " + annotationName + " handler: " + e.getMessage());
                }
            }
        }

        super.visitEnd();
    }

    private boolean hasAnnotation(String simpleName) {
        String fullName = "com.github.rickmvi.jtoolbox.annotation.processor.annotations." + simpleName;

        return classElement.getAnnotationMirrors().stream()
                .anyMatch(mirror -> mirror.getAnnotationType()
                        .toString().equals(fullName));
    }

    /**
     * Informações coletadas sobre a classe durante a visita
     */
    public static class ClassInfo {
        private final String name;
        private final String superName;
        private final int access;
        private final Map<String, FieldData> fields = new java.util.HashMap<>();
        private final Map<String, MethodData> methods = new java.util.HashMap<>();

        public ClassInfo(String name, String superName, int access) {
            this.name = name;
            this.superName = superName;
            this.access = access;
        }

        public void addField(String name, String descriptor, int access) {
            fields.put(name, new FieldData(name, descriptor, access));
        }

        public void addMethod(String name, String descriptor, int access) {
            String key = name + descriptor;
            methods.put(key, new MethodData(name, descriptor, access));
        }

        public boolean hasMethod(String name, String descriptor) {
            return methods.containsKey(name + descriptor);
        }

        public boolean hasGetter(String fieldName) {
            String getterName = "get" + capitalize(fieldName);
            return methods.values().stream()
                    .anyMatch(m -> m.name.equals(getterName));
        }

        public boolean hasSetter(String fieldName) {
            String setterName = "set" + capitalize(fieldName);
            return methods.values().stream()
                    .anyMatch(m -> m.name.equals(setterName));
        }

        public Map<String, FieldData> getFields() {
            return fields;
        }

        public String getName() {
            return name;
        }

        public String getSuperName() {
            return superName;
        }

        public int getAccess() {
            return access;
        }

        private static String capitalize(String str) {
            if (str == null || str.isEmpty()) return str;
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
    }

    public static class FieldData {
        public final String name;
        public final String descriptor;
        public final int access;

        public FieldData(String name, String descriptor, int access) {
            this.name = name;
            this.descriptor = descriptor;
            this.access = access;
        }

        public boolean isFinal() {
            return (access & Opcodes.ACC_FINAL) != 0;
        }

        public boolean isStatic() {
            return (access & Opcodes.ACC_STATIC) != 0;
        }
    }

    public static class MethodData {
        public final String name;
        public final String descriptor;
        public final int access;

        public MethodData(String name, String descriptor, int access) {
            this.name = name;
            this.descriptor = descriptor;
            this.access = access;
        }
    }
}