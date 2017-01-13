package com.codepoetics.vaporetto;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class Property<T, V> {

    public static <T extends VType<T>, V> Property<T, V> of(Class<? extends T> cls, Function<? super T, ? extends V> getter) {
        PropertyInfo<V> info = VaporettoInfo.forClass(cls).getPropertyInfo(getter);
        return new Property<>(
                info.getName(),
                cls,
                info.getType(),
                getter,
                (t, v) -> t.update(b -> b.with(getter, v)));
    }

    public static <T extends VType<T>, V extends VType<V>, V2> Property<T, V2> of(Property<T, V> property, Function<? super V, ? extends V2> getter) {
        return property.chain(Property.of(property.valueType, getter));
    }

    private final String name;
    private final Class<? extends T> targetType;
    private final Class<? extends V> valueType;
    private final Function<? super T, ? extends V> getter;
    private final BiFunction<T, V, T> updater;

    private Property(String name, Class<? extends T> targetType, Class<? extends V> valueType, Function<? super T, ? extends V> getter, BiFunction<T, V, T> updater) {
        this.name = name;
        this.targetType = targetType;
        this.valueType = valueType;
        this.getter = getter;
        this.updater = updater;
    }

    public V get(T target) {
        return getter.apply(target);
    }

    public T update(T target, V newValue) {
        return updater.apply(target, newValue);
    }

    public <V2> Property<T, V2> chain(Property<V, V2> nextProperty) {
        return new Property<>(
                name + "." + nextProperty.name,
                targetType,
                nextProperty.valueType,
                getter.andThen(nextProperty.getter),
                (t, v2) -> update(t, nextProperty.update(get(t), v2)));
    }

    public String getName() {
        return name;
    }
}
