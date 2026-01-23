package com.github.rickmvi.jtoolbox.annotation.processor.util;

public class CodeGenUtils {

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String getGetterName(String fieldName, String typeName) {
        String prefix = typeName.equals("boolean") || typeName.equals("java.lang.Boolean")
                ? "is"
                : "get";
        return prefix + capitalize(fieldName);
    }

    public static String getSetterName(String fieldName) {
        return "set" + capitalize(fieldName);
    }

    public static String escapeString(String str) {
        if (str == null) return "null";
        return "\"" + str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t") + "\"";
    }

    public static String getSimpleTypeName(String qualifiedName) {
        int lastDot = qualifiedName.lastIndexOf('.');
        return lastDot >= 0 ? qualifiedName.substring(lastDot + 1) : qualifiedName;
    }

    public static boolean isPrimitive(String typeName) {
        return typeName.equals("byte") || typeName.equals("short") ||
                typeName.equals("int") || typeName.equals("long") ||
                typeName.equals("float") || typeName.equals("double") ||
                typeName.equals("boolean") || typeName.equals("char");
    }

    public static String getDefaultValue(String typeName) {
        if (isPrimitive(typeName)) {
            if (typeName.equals("boolean")) return "false";
            if (typeName.equals("char")) return "'\\0'";
            return "0";
        }
        return "null";
    }
}