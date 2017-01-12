package com.codepoetics.vaporetto;

import java.lang.reflect.Method;

final class PropertyInfo<V> {

    @SuppressWarnings("unchecked")
    static <V> PropertyInfo<V> forMethod(Method method, int slotIndex) {
        return new PropertyInfo<>(PropertyNaming.forMethod(method), (Class<? extends V>) method.getReturnType(), slotIndex);
    }

    private final String name;
    private final Class<? extends V> type;
    private final int slotIndex;

    PropertyInfo(String name, Class<? extends V> type, int slotIndex) {
        this.name = name;
        this.type = type;
        this.slotIndex = slotIndex;
    }

    public String getName() {
        return name;
    }

    public Class<? extends V> getType() {
        return type;
    }

    public int getSlotIndex() {
        return slotIndex;
    }
}
