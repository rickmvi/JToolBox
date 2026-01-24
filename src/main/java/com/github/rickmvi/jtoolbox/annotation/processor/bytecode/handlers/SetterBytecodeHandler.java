package com.github.rickmvi.jtoolbox.annotation.processor.bytecode.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.BytecodeAnnotationProcessor.ProcessingContext;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.ClassInfo;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.FieldData;
import com.github.rickmvi.jtoolbox.annotation.processor.util.CodeGenUtils;
import org.jetbrains.annotations.Contract;
import org.objectweb.asm.*;

import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * Gera métodos setter usando manipulação de bytecode.
 */
public class SetterBytecodeHandler implements BytecodeHandler {

    private final ProcessingContext context;

    public SetterBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        Map<String, FieldData> fields = classInfo.getFields();

        for (FieldData field : fields.values()) {
            // Pula campos estáticos ou finais
            if (field.isStatic() || field.isFinal()) {
                continue;
            }

            // Verifica se setter já existe
            if (classInfo.hasSetter(field.name)) {
                continue;
            }

            generateSetter(cv, classInfo, field);
        }
    }

    private void generateSetter(ClassVisitor cv, ClassInfo classInfo, FieldData field) {
        String setterName = "set" + capitalize(field.name);
        String descriptor = "(" + field.descriptor + ")V";

        // Cria o método setter
        // public void setFieldName(Type value) { this.fieldName = value; }
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                setterName,
                descriptor,
                null,
                null
        );

        mv.visitCode();

        // this
        mv.visitVarInsn(Opcodes.ALOAD, 0);

        // Carrega o parâmetro (value)
        int loadOpcode = getLoadOpcode(field.descriptor);
        mv.visitVarInsn(loadOpcode, 1);

        // this.fieldName = value
        mv.visitFieldInsn(
                Opcodes.PUTFIELD,
                classInfo.getName(),
                field.name,
                field.descriptor
        );

        // return
        mv.visitInsn(Opcodes.RETURN);

        mv.visitMaxs(0, 0); // Calculado automaticamente
        mv.visitEnd();

        context.note(null, "Generated setter: " + setterName + "(" + field.descriptor + ")");
    }

    @Contract(pure = true)
    private int getLoadOpcode(String descriptor) {
        return switch (descriptor.charAt(0)) {
            case 'Z', 'B', 'C', 'S', 'I' -> Opcodes.ILOAD;
            case 'J' -> Opcodes.LLOAD;
            case 'F' -> Opcodes.FLOAD;
            case 'D' -> Opcodes.DLOAD;
            default -> Opcodes.ALOAD; // Object reference
        };
    }

    private static String capitalize(String str) {
        return CodeGenUtils.capitalize(str);
    }
}