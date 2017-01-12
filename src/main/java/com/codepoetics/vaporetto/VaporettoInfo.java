package com.codepoetics.vaporetto;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

final class VaporettoInfo<T> {

    private static final ConcurrentMap<Class<?>, VaporettoInfo<?>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T> VaporettoInfo<T> forClass(Class<? extends T> cls) {
        return (VaporettoInfo<T>) cache.computeIfAbsent(cls, VaporettoInfo::forClassUncached);
    }

    private static <T> VaporettoInfo<T> forClassUncached(Class<? extends T> cls) {
        MethodRecordingProxy<T> proxy = MethodRecordingProxy.forClass(cls);

        Method[] methods = getMethodsInNameOrder(cls);

        Map<Method, PropertyInfo<?>> methodMap = new HashMap<>();
        String[] propertyNames = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            PropertyInfo<Object> propertyInfo = PropertyInfo.forMethod(methods[i], i);
            methodMap.put(methods[i], propertyInfo);
            propertyNames[i] = propertyInfo.getName();
        }

        return new VaporettoInfo<>(cls, proxy, methodMap, propertyNames);
    }

    private static <T> Method[] getMethodsInNameOrder(Class<? extends T> cls) {
        return Stream.of(cls.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
                .sorted(Comparator.comparing(PropertyNaming::forMethod))
                .toArray(Method[]::new);
    }

    private final Class<? extends T> type;
    private final MethodRecordingProxy<T> proxy;
    private final Map<Method, PropertyInfo<?>> methodMap;
    private final String[] propertyNames;

    private VaporettoInfo(Class<? extends T> type, MethodRecordingProxy<T> proxy, Map<Method, PropertyInfo<?>> methodMap, String[] propertyNames) {
        this.type = type;
        this.proxy = proxy;
        this.methodMap = methodMap;
        this.propertyNames = propertyNames;
    }

    <V> PropertyInfo<V> getPropertyInfo(Function<? super T, ? extends V> property) {
        property.apply(proxy.getProxy());
        return getPropertyInfo(proxy.getLastMethodCalled());
    }

    @SuppressWarnings("unchecked")
    private <V> PropertyInfo<V> getPropertyInfo(Method method) {
        return (PropertyInfo<V>) methodMap.get(method);
    }

    <V> Consumer<Object[]> getSlotWriter(Function<? super T, ? extends V> property, V value) {
        property.apply(proxy.getProxy());
        int slotIndex = methodMap.get(proxy.getLastMethodCalled()).getSlotIndex();
        return slots -> slots[slotIndex] = value;
    }

    @SuppressWarnings("unchecked")
    <V> V readSlot(Object[] slots, Method method) {
        PropertyInfo<V> info = getPropertyInfo(method);
        Object value = slots[info.getSlotIndex()];
        return value == null
                ? ZeroValue.castForType(info.getType())
                : (V) value;
    }

    Map<String, Object> toPropertyMap(Object[] slots) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < slots.length; i++) {
            result.put(propertyNames[i], slots[i]);
        }
        return result;
    }

    String toString(Object[] slots) {
        boolean first = true;
        StringBuilder s = new StringBuilder()
                .append("{");
        for (int i = 0; i < slots.length; i++) {
            if (first) {
                first = false;
            } else {
                s.append(",");
            }
            s.append(propertyNames[i]).append("=").append(slots[i]);
        }
        return s.append("}").toString();
    }

    Object[] getEmptySlots() {
        return new Object[propertyNames.length];
    }

    T makeProxy(InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[] { type, VProxy.class },
                handler
        ));
    }

    public T createProxy(Object[] slots) {
        return makeProxy(VaporettoProxy.of(this, slots));
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
                (o instanceof VaporettoInfo
                && VaporettoInfo.class.cast(o).type.equals(type));
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
