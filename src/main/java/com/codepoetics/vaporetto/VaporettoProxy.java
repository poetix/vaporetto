package com.codepoetics.vaporetto;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

final class VaporettoProxy<T> implements InvocationHandler, VType<T>, VProxy {

    static <T> VaporettoProxy<T> of(VaporettoInfo<T> info, Object[] slots) {
        return new VaporettoProxy<>(info, slots);
    }

    private final VaporettoInfo<T> info;
    private final Object[] slots;
    private Object proxy;

    private VaporettoProxy(VaporettoInfo<T> info, Object[] slots) {
        this.info = info;
        this.slots = slots;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.proxy = proxy;

        if (method.getDeclaringClass().isAssignableFrom(getClass())) {
            return method.invoke(this, args);
        }

        return info.readSlot(slots, method);
    }

    @Override
    public Map<String, Object> getProperties() {
        return info.toPropertyMap(slots);
    }

    @Override
    public T update(UnaryOperator<Vaporetto<T>> builder) {
        return Vaporetto.build(info, Arrays.copyOf(slots, slots.length), builder);
    }

    @Override
    public boolean equals(Object o) {
        return o == proxy ||
                (o instanceof VType
                    && VProxy.class.cast(o).hasSame(info, slots));
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, Arrays.deepHashCode(slots));
    }

    @Override
    public String toString() {
        return info.toString(slots);
    }

    @Override
    public boolean hasSame(VaporettoInfo<?> info, Object[] slots) {
        return this.info.equals(info) && Arrays.deepEquals(this.slots, slots);
    }
}
