package com.github.rickmvi.console.convert;

@lombok.experimental.UtilityClass
public class ObjectToNumber {

    @SuppressWarnings("ConstantConditions")
    public int toInt(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        return Integer.parseInt(String.valueOf(o));
    }

    @SuppressWarnings("ConstantConditions")
    public long toLong(Object o) {
        if (o instanceof Number) return ((Number) o).longValue();
        return Long.parseLong(String.valueOf(o));
    }

    @SuppressWarnings("ConstantConditions")
    public float toFloat(Object o) {
        if (o instanceof Number) return ((Number) o).floatValue();
        return Float.parseFloat(String.valueOf(o));
    }

    @SuppressWarnings("ConstantConditions")
    public double toDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        return Double.parseDouble(String.valueOf(o));
    }

    @SuppressWarnings("ConstantConditions")
    public boolean toBoolean(Object o) {
        if (o instanceof Boolean) return (Boolean) o;
        return Boolean.parseBoolean(String.valueOf(o));
    }
}
