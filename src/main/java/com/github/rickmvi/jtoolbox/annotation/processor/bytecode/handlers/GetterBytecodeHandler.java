package com.github.rickmvi.jtoolbox.annotation.processor.bytecode.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.BytecodeAnnotationProcessor.ProcessingContext;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.ClassInfo;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.FieldData;
import com.github.rickmvi.jtoolbox.annotation.processor.util.CodeGenUtils;
import org.objectweb.asm.*;

import javax.lang.model.element.TypeElement;
import java.util.Map;

public class GetterBytecodeHandler implements BytecodeHandler {

    private final ProcessingContext context;

    public GetterBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        Map<String, FieldData> fields = classInfo.getFields();

        for (FieldData field : fields.values()) {
            // Pula campos estáticos
            if (field.isStatic()) {
                continue;
            }

            // Verifica se getter já existe
            if (classInfo.hasGetter(field.name)) {
                continue;
            }

            generateGetter(cv, classInfo, field);
        }
    }

    private void generateGetter(ClassVisitor cv, ClassInfo classInfo, FieldData field) {
        String getterName = getGetterName(field);
        String returnDescriptor = field.descriptor;

        // Cria o método getter
        // public Type getFieldName() { return this.fieldName; }
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                getterName,
                "()" + returnDescriptor,
                null,
                null
        );

        mv.visitCode();

        // this
        mv.visitVarInsn(Opcodes.ALOAD, 0);

        // this.fieldName
        mv.visitFieldInsn(
                Opcodes.GETFIELD,
                classInfo.getName(),
                field.name,
                field.descriptor
        );

        // return
        int returnOpcode = getReturnOpcode(field.descriptor);
        mv.visitInsn(returnOpcode);

        mv.visitMaxs(0, 0); // Calculado automaticamente
        mv.visitEnd();

        context.note(null, "Generated getter: " + getterName + "()");
    }

    private String getGetterName(FieldData field) {
        // Para boolean: isFieldName, para outros: getFieldName
        boolean isBoolean = field.descriptor.equals("Z");
        String prefix = isBoolean ? "is" : "get";

        return prefix + capitalize(field.name);
    }

    private int getReturnOpcode(String descriptor) {
        return switch (descriptor.charAt(0)) {
            case 'V' -> Opcodes.RETURN;
            case 'Z', 'B', 'C', 'S', 'I' -> Opcodes.IRETURN;
            case 'J' -> Opcodes.LRETURN;
            case 'F' -> Opcodes.FRETURN;
            case 'D' -> Opcodes.DRETURN;
            default -> Opcodes.ARETURN; // Object reference
        };
    }

    private static String capitalize(String str) {
        return CodeGenUtils.capitalize(str);
    }
}