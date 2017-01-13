package com.codepoetics.vaporetto;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Vaporetto<T> implements Supplier<T> {

    public static <T> T build(Class<? extends T> cls, UnaryOperator<Vaporetto<T>> builder) {
        final VaporettoInfo<T> info = VaporettoInfo.forClass(cls);
        return build(info, info.getEmptySlots(), builder);
    }

    static <T> T build(VaporettoInfo<T> info, Object[] slots, UnaryOperator<Vaporetto<T>> builder) {
        Vaporetto<T> vaporetto = new Vaporetto<>(info, slots);
        return builder.apply(vaporetto).get();
    }

    private final VaporettoInfo<T> info;
    private final List<Consumer<Object[]>> slotWriters = new ArrayList<>();
    private final Object[] slots;

    private Vaporetto(VaporettoInfo<T> info, Object[] slots) {
        this.info = info;
        this.slots = slots;
    }

    public <V> Vaporetto<T> with(Function<? super T, ? extends V> property, V value) {
        slotWriters.add(info.getSlotWriter(property, value));
        return this;
    }

    @SafeVarargs
    public final <V> Vaporetto<T> with(Function<? super T, ? extends Collection<? extends V>> property, V...values) {
        PropertyInfo<Collection<? extends V>> propertyInfo = info.getPropertyInfo(property);
        Collection<? extends V> collection = makeCollection(propertyInfo.getType(), values);
        slotWriters.add((slots) -> slots[propertyInfo.getSlotIndex()] = collection);
        return this;
    }

    private <V> Collection<? extends V> makeCollection(Class<? extends Collection<? extends V>> cls, V[] values) {
        if (cls.isAssignableFrom(List.class)) {
            return Arrays.asList(values);
        }
        if (cls.isAssignableFrom(Set.class)) {
            return Stream.of(values).collect(Collectors.toSet());
        }
        throw new UnsupportedOperationException("Collection type " + cls + " is not supported");
    }

    public <V> Vaporetto<T> with(Function<? super T, ? extends V> property, UnaryOperator<Vaporetto<V>> builder) {
        return with(property, build(info.getPropertyInfo(property).getType(), builder));
    }

    @Override
    public T get() {
        slotWriters.forEach(writer -> writer.accept(slots));
        return info.createProxy(slots);
    }
}
