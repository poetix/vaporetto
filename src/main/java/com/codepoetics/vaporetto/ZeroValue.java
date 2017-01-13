package com.codepoetics.vaporetto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ZeroValue {

    @SuppressWarnings("unchecked")
    static <V> V castForType(Class<? extends V> type) {
        return (V) forType(type);
    }

    static Object forType(Class<?> type) {
        if (type.isAssignableFrom(Map.class)) {
            return Collections.emptyMap();
        }
        if (type.isAssignableFrom(List.class)) {
            return Collections.emptyList();
        }
        if (type.isAssignableFrom(Set.class)) {
            return Collections.emptySet();
        }
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
