package com.codepoetics.vaporetto;

final class ZeroValue {

    @SuppressWarnings("unchecked")
    static <V> V castForType(Class<? extends V> type) {
        return (V) forType(type);
    }

    static Object forType(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == char.class) {
            return (char) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0d;
        }
        if (type == double.class) {
            return 0d;
        }
        throw new IllegalArgumentException("Cannot determine zero return value for type " + type);
    }

}
