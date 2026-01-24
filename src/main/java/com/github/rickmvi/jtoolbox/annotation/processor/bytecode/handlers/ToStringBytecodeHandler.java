package com.github.rickmvi.jtoolbox.annotation.processor.bytecode.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.BytecodeAnnotationProcessor.ProcessingContext;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.ClassInfo;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.FieldData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;

import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * Gera método toString() usando manipulação de bytecode.
 */
public class ToStringBytecodeHandler implements BytecodeHandler {

    private final ProcessingContext context;

    public ToStringBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        // Verifica se toString já existe
        if (classInfo.hasMethod("toString", "()Ljava/lang/String;")) {
            return;
        }

        generateToString(cv, classInfo);
    }

    private void generateToString(ClassVisitor cv, ClassInfo classInfo) {
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "toString",
                "()Ljava/lang/String;",
                null,
                null
        );

        mv.visitCode();

        Map<String, FieldData> fields = classInfo.getFields();

        // new StringBuilder()
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/lang/StringBuilder",
                "<init>",
                "()V",
                false
        );

        // ClassName(
        String className = classInfo.getName().substring(
                classInfo.getName().lastIndexOf('/') + 1
        );
        mv.visitLdcInsn(className + "(");
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
        );

        // Adiciona cada campo
        boolean first = true;
        for (FieldData field : fields.values()) {
            if (field.isStatic()) continue;

            if (!first) {
                mv.visitLdcInsn(", ");
                mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        "java/lang/StringBuilder",
                        "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                        false
                );
            }
            first = false;

            // fieldName=
            mv.visitLdcInsn(field.name + "=");
            mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                    false
            );

            // this.fieldName
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(
                    Opcodes.GETFIELD,
                    classInfo.getName(),
                    field.name,
                    field.descriptor
            );

            // append(value)
            String appendDesc = getAppendDescriptor(field.descriptor);
            mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    appendDesc,
                    false
            );
        }

        // )
        mv.visitLdcInsn(")");
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
        );

        // toString()
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "toString",
                "()Ljava/lang/String;",
                false
        );

        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        context.note(null, "Generated toString() method");
    }

    @Contract(pure = true)
    private @NotNull String getAppendDescriptor(@NotNull String fieldDescriptor) {
        return switch (fieldDescriptor.charAt(0)) {
            case 'Z' -> "(Z)Ljava/lang/StringBuilder;";
            case 'B', 'S', 'I' -> "(I)Ljava/lang/StringBuilder;";
            case 'C' -> "(C)Ljava/lang/StringBuilder;";
            case 'J' -> "(J)Ljava/lang/StringBuilder;";
            case 'F' -> "(F)Ljava/lang/StringBuilder;";
            case 'D' -> "(D)Ljava/lang/StringBuilder;";
            default -> "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";
        };
    }
}