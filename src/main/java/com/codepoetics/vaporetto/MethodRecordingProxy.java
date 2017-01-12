package com.codepoetics.vaporetto;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

final class MethodRecordingProxy<T> implements InvocationHandler {

    static <T> MethodRecordingProxy<T> forClass(Class<? extends T> cls) {
        return new MethodRecordingProxy<T>().initialise(cls);
    }

    private T proxy;
    private final ThreadLocal<Method> lastMethodCalled = new ThreadLocal<>();

    T getProxy() {
        return proxy;
    }

    Method getLastMethodCalled() {
        return lastMethodCalled.get();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().isAssignableFrom(getClass())) {
            return method.invoke(this, args);
        }

        lastMethodCalled.set(method);
        return ZeroValue.forType(method.getReturnType());
    }

    private MethodRecordingProxy<T> initialise(Class<? extends T> cls) {
        proxy = cls.cast(Proxy.newProxyInstance(cls.getClassLoader(),
                new Class<?>[] { cls },
                this));
        return this;
    }


}
