package com.github.rickmvi.jtoolbox.annotation.processor.bytecode.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.BytecodeAnnotationProcessor.ProcessingContext;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.ClassInfo;
import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.FieldData;
import org.objectweb.asm.*;

import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.stream.Collectors;

// ============================================
// Data Handler - Combina Getter + Setter + ToString + EqualsAndHashCode
// ============================================
public class DataBytecodeHandler implements BytecodeHandler {
    private final ProcessingContext context;
    private final GetterBytecodeHandler getterHandler;
    private final SetterBytecodeHandler setterHandler;
    private final ToStringBytecodeHandler toStringHandler;
    private final EqualsAndHashCodeBytecodeHandler equalsHashCodeHandler;

    public DataBytecodeHandler(ProcessingContext context) {
        this.context = context;
        this.getterHandler = new GetterBytecodeHandler(context);
        this.setterHandler = new SetterBytecodeHandler(context);
        this.toStringHandler = new ToStringBytecodeHandler(context);
        this.equalsHashCodeHandler = new EqualsAndHashCodeBytecodeHandler(context);
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        getterHandler.transform(cv, element, classInfo);
        setterHandler.transform(cv, element, classInfo);
        toStringHandler.transform(cv, element, classInfo);
        equalsHashCodeHandler.transform(cv, element, classInfo);
    }
}

// ============================================
// EqualsAndHashCode Handler
// ============================================
class EqualsAndHashCodeBytecodeHandler implements BytecodeHandler {
    private final ProcessingContext context;

    public EqualsAndHashCodeBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        if (!classInfo.hasMethod("equals", "(Ljava/lang/Object;)Z")) {
            generateEquals(cv, classInfo);
        }

        if (!classInfo.hasMethod("hashCode", "()I")) {
            generateHashCode(cv, classInfo);
        }
    }

    private void generateEquals(ClassVisitor cv, ClassInfo classInfo) {
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "equals",
                "(Ljava/lang/Object;)Z",
                null,
                null
        );

        mv.visitCode();

        Label returnTrue = new Label();
        Label returnFalse = new Label();
        Label checkFields = new Label();

        // if (this == obj) return true;
        mv.visitVarInsn(Opcodes.ALOAD, 0); // this
        mv.visitVarInsn(Opcodes.ALOAD, 1); // obj
        mv.visitJumpInsn(Opcodes.IF_ACMPEQ, returnTrue);

        // if (obj == null) return false;
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitJumpInsn(Opcodes.IFNULL, returnFalse);

        // if (getClass() != obj.getClass()) return false;
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass",
                "()Ljava/lang/Class;", false);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass",
                "()Ljava/lang/Class;", false);
        mv.visitJumpInsn(Opcodes.IF_ACMPNE, returnFalse);

        // Cast: ClassName other = (ClassName) obj;
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitTypeInsn(Opcodes.CHECKCAST, classInfo.getName());
        mv.visitVarInsn(Opcodes.ASTORE, 2); // store in local var 2

        // Compare fields
        List<FieldData> fields = classInfo.getFields().values().stream()
                .filter(f -> !f.isStatic())
                .collect(Collectors.toList());

        for (FieldData field : fields) {
            compareField(mv, classInfo, field, returnFalse);
        }

        // All checks passed - return true
        mv.visitLabel(returnTrue);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.IRETURN);

        // Return false
        mv.visitLabel(returnFalse);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitInsn(Opcodes.IRETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        context.note(null, "Generated equals() method");
    }

    private void compareField(MethodVisitor mv, ClassInfo classInfo, FieldData field, Label returnFalse) {
        String descriptor = field.descriptor;

        if (isPrimitive(descriptor)) {
            // Primitive comparison
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD, classInfo.getName(), field.name, descriptor);
            mv.visitVarInsn(Opcodes.ALOAD, 2); // other
            mv.visitFieldInsn(Opcodes.GETFIELD, classInfo.getName(), field.name, descriptor);

            if (descriptor.equals("J")) { // long
                mv.visitInsn(Opcodes.LCMP);
                mv.visitJumpInsn(Opcodes.IFNE, returnFalse);
            } else if (descriptor.equals("F")) { // float
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "compare",
                        "(FF)I", false);
                mv.visitJumpInsn(Opcodes.IFNE, returnFalse);
            } else if (descriptor.equals("D")) { // double
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "compare",
                        "(DD)I", false);
                mv.visitJumpInsn(Opcodes.IFNE, returnFalse);
            } else { // int, short, byte, boolean, char
                mv.visitJumpInsn(Opcodes.IF_ICMPNE, returnFalse);
            }
        } else {
            // Object comparison using Objects.equals
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this.field
            mv.visitFieldInsn(Opcodes.GETFIELD, classInfo.getName(), field.name, descriptor);
            mv.visitVarInsn(Opcodes.ALOAD, 2); // other.field
            mv.visitFieldInsn(Opcodes.GETFIELD, classInfo.getName(), field.name, descriptor);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "equals",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(Opcodes.IFEQ, returnFalse);
        }
    }

    private void generateHashCode(ClassVisitor cv, ClassInfo classInfo) {
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "hashCode",
                "()I",
                null,
                null
        );

        mv.visitCode();

        List<FieldData> fields = classInfo.getFields().values().stream()
                .filter(f -> !f.isStatic())
                .collect(Collectors.toList());

        if (fields.isEmpty()) {
            // return 0;
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);
        } else {
            // Use Objects.hash(field1, field2, ...)
            mv.visitIntInsn(Opcodes.BIPUSH, fields.size());
            mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");

            int index = 0;
            for (FieldData field : fields) {
                mv.visitInsn(Opcodes.DUP);
                mv.visitIntInsn(Opcodes.BIPUSH, index);

                // Load field value
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, classInfo.getName(),
                        field.name, field.descriptor);

                // Box primitive if needed
                if (isPrimitive(field.descriptor)) {
                    boxPrimitive(mv, field.descriptor);
                }

                mv.visitInsn(Opcodes.AASTORE);
                index++;
            }

            // Objects.hash(array)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "hash",
                    "([Ljava/lang/Object;)I", false);
            mv.visitInsn(Opcodes.IRETURN);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        context.note(null, "Generated hashCode() method");
    }

    private boolean isPrimitive(String descriptor) {
        char c = descriptor.charAt(0);
        return c == 'Z' || c == 'B' || c == 'C' || c == 'S' ||
                c == 'I' || c == 'J' || c == 'F' || c == 'D';
    }

    private void boxPrimitive(MethodVisitor mv, String descriptor) {
        switch (descriptor.charAt(0)) {
            case 'Z' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean",
                    "valueOf", "(Z)Ljava/lang/Boolean;", false);
            case 'B' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte",
                    "valueOf", "(B)Ljava/lang/Byte;", false);
            case 'C' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character",
                    "valueOf", "(C)Ljava/lang/Character;", false);
            case 'S' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short",
                    "valueOf", "(S)Ljava/lang/Short;", false);
            case 'I' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer",
                    "valueOf", "(I)Ljava/lang/Integer;", false);
            case 'J' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long",
                    "valueOf", "(J)Ljava/lang/Long;", false);
            case 'F' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float",
                    "valueOf", "(F)Ljava/lang/Float;", false);
            case 'D' -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double",
                    "valueOf", "(D)Ljava/lang/Double;", false);
        }
    }
}

// ============================================
// AllArgsConstructor Handler
// ============================================
class AllArgsConstructorBytecodeHandler implements BytecodeHandler {
    private final ProcessingContext context;

    public AllArgsConstructorBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        List<FieldData> fields = classInfo.getFields().values().stream()
                .filter(f -> !f.isStatic())
                .collect(Collectors.toList());

        if (fields.isEmpty()) {
            context.note(null, "No fields to generate AllArgsConstructor");
            return;
        }

        String descriptor = buildConstructorDescriptor(fields);

        if (classInfo.hasMethod("<init>", descriptor)) {
            context.note(null, "AllArgsConstructor already exists");
            return;
        }

        generateConstructor(cv, classInfo, fields, descriptor);
    }

    private void generateConstructor(ClassVisitor cv, ClassInfo classInfo,
                                     List<FieldData> fields, String descriptor) {
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                descriptor,
                null,
                null
        );

        mv.visitCode();

        // Call super()
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classInfo.getSuperName(),
                "<init>", "()V", false);

        // Assign each parameter to corresponding field
        int paramIndex = 1; // 0 is 'this'
        for (FieldData field : fields) {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this

            // Load parameter
            int loadOpcode = getLoadOpcode(field.descriptor);
            mv.visitVarInsn(loadOpcode, paramIndex);

            // this.field = param
            mv.visitFieldInsn(Opcodes.PUTFIELD, classInfo.getName(),
                    field.name, field.descriptor);

            paramIndex += getTypeSize(field.descriptor);
        }

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        context.note(null, "Generated AllArgsConstructor with " + fields.size() + " parameters");
    }

    private String buildConstructorDescriptor(List<FieldData> fields) {
        StringBuilder sb = new StringBuilder("(");
        for (FieldData field : fields) {
            sb.append(field.descriptor);
        }
        sb.append(")V");
        return sb.toString();
    }

    private int getLoadOpcode(String descriptor) {
        return switch (descriptor.charAt(0)) {
            case 'Z', 'B', 'C', 'S', 'I' -> Opcodes.ILOAD;
            case 'J' -> Opcodes.LLOAD;
            case 'F' -> Opcodes.FLOAD;
            case 'D' -> Opcodes.DLOAD;
            default -> Opcodes.ALOAD;
        };
    }

    private int getTypeSize(String descriptor) {
        char c = descriptor.charAt(0);
        return (c == 'J' || c == 'D') ? 2 : 1;
    }
}

// ============================================
// NoArgsConstructor Handler
// ============================================
class NoArgsConstructorBytecodeHandler implements BytecodeHandler {
    private final ProcessingContext context;

    public NoArgsConstructorBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        if (classInfo.hasMethod("<init>", "()V")) {
            context.note(null, "NoArgsConstructor already exists");
            return;
        }

        generateNoArgsConstructor(cv, classInfo);
    }

    private void generateNoArgsConstructor(ClassVisitor cv, ClassInfo classInfo) {
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
        );

        mv.visitCode();

        // Call super()
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classInfo.getSuperName(),
                "<init>", "()V", false);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        context.note(null, "Generated NoArgsConstructor");
    }
}

// ============================================
// RequiredArgsConstructor Handler
// ============================================
class RequiredArgsConstructorBytecodeHandler implements BytecodeHandler {
    private final ProcessingContext context;

    public RequiredArgsConstructorBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        // Select only final and @NonNull fields
        List<FieldData> requiredFields = classInfo.getFields().values().stream()
                .filter(f -> !f.isStatic() && f.isFinal())
                .collect(Collectors.toList());

        if (requiredFields.isEmpty()) {
            context.note(null, "No required fields (final) for RequiredArgsConstructor");
            return;
        }

        String descriptor = buildConstructorDescriptor(requiredFields);

        if (classInfo.hasMethod("<init>", descriptor)) {
            context.note(null, "RequiredArgsConstructor already exists");
            return;
        }

        generateConstructor(cv, classInfo, requiredFields, descriptor);
    }

    private void generateConstructor(ClassVisitor cv, ClassInfo classInfo,
                                     List<FieldData> fields, String descriptor) {
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                descriptor,
                null,
                null
        );

        mv.visitCode();

        // Call super()
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classInfo.getSuperName(),
                "<init>", "()V", false);

        // Assign parameters to fields
        int paramIndex = 1;
        for (FieldData field : fields) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);

            int loadOpcode = getLoadOpcode(field.descriptor);
            mv.visitVarInsn(loadOpcode, paramIndex);

            mv.visitFieldInsn(Opcodes.PUTFIELD, classInfo.getName(),
                    field.name, field.descriptor);

            paramIndex += getTypeSize(field.descriptor);
        }

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        context.note(null, "Generated RequiredArgsConstructor with " +
                fields.size() + " final fields");
    }

    private String buildConstructorDescriptor(List<FieldData> fields) {
        StringBuilder sb = new StringBuilder("(");
        for (FieldData field : fields) {
            sb.append(field.descriptor);
        }
        sb.append(")V");
        return sb.toString();
    }

    private int getLoadOpcode(String descriptor) {
        return switch (descriptor.charAt(0)) {
            case 'Z', 'B', 'C', 'S', 'I' -> Opcodes.ILOAD;
            case 'J' -> Opcodes.LLOAD;
            case 'F' -> Opcodes.FLOAD;
            case 'D' -> Opcodes.DLOAD;
            default -> Opcodes.ALOAD;
        };
    }

    private int getTypeSize(String descriptor) {
        char c = descriptor.charAt(0);
        return (c == 'J' || c == 'D') ? 2 : 1;
    }
}

// ============================================
// Builder Handler
// ============================================
class BuilderBytecodeHandler implements BytecodeHandler {
    private final ProcessingContext context;

    public BuilderBytecodeHandler(ProcessingContext context) {
        this.context = context;
    }

    @Override
    public void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo) {
        // Generate static builder() method
        generateBuilderMethod(cv, classInfo);

        context.note(null, "Builder pattern requires separate Builder class generation - " +
                "consider using @AllArgsConstructor with fluent setters instead");
    }

    private void generateBuilderMethod(ClassVisitor cv, ClassInfo classInfo) {
        // Check if builder() method already exists
        if (classInfo.hasMethod("builder", "()L" + classInfo.getName() + "Builder;")) {
            return;
        }

        String builderClassName = classInfo.getName() + "Builder";

        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "builder",
                "()L" + builderClassName + ";",
                null,
                null
        );

        mv.visitCode();

        // return new XxxBuilder();
        mv.visitTypeInsn(Opcodes.NEW, builderClassName);
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, builderClassName,
                "<init>", "()V", false);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        context.note(null, "Generated static builder() method - " +
                "Builder class must be created separately");
    }
}