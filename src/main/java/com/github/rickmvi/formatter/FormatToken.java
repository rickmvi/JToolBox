package com.github.rickmvi.formatter;

import com.github.rickmvi.console.convert.ObjectToNumber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Locale;

public enum FormatToken {
    STRING("s") {
        @Override public String format(Object args1) {
            return String.valueOf(args1);
        }
    },
    INT("i") {
        @Override public String format(Object value) {
            return String.valueOf(ObjectToNumber.toInt(value));
        }
    },
    LONG("l") {
        @Override public String format(Object value) {
            return String.valueOf(ObjectToNumber.toLong(value));
        }
    },
    DECIMAL("#d") {
        @Override public String format(Object value) {
            return String.format(Locale.US, "%.2f", ObjectToNumber.toDouble(value));
        }
    },
    EXPONENTIAL("ed") {
        @Override public String format(Object value) {
            return String.format(Locale.US, "%.2e", ObjectToNumber.toDouble(value));
        }
    },
    NUMBER("n") {
        private final DecimalFormat format = new DecimalFormat("#,##0.00");
        @Override public String format(Object value) {
            return format.format(ObjectToNumber.toDouble(value));
        }
    },
    UPPER("u") {
        @Override public String format(Object value) {
            return String.valueOf(value).toUpperCase(Locale.ROOT);
        }
    },
    LOWER("lc") {
        @Override public String format(Object value) {
            return String.valueOf(value).toLowerCase(Locale.ROOT);
        }
    },
    CHAR("c") {
        @Override public String format(Object value) {
            return String.valueOf(value).charAt(0) + "";
        }
    },
    BOOLEAN("b") {
        @Override public String format(Object value) {
            return String.valueOf(Boolean.parseBoolean(String.valueOf(value)));
        }
    };

    private final String code;

    @Contract(pure = true)
    FormatToken(String code) {
        this.code = code;
    }

    public abstract String format(Object value);

    public static @Nullable FormatToken fromCode(String code) {
        for (FormatToken token : FormatToken.values()) {
            if (token.code.equals(code)) {
                return token;
            }
        }
        return null;
    }
}
