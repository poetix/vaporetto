package com.codepoetics.vaporetto;

import java.lang.reflect.Method;

final class PropertyNaming {

    static String forMethod(Method method) {
        return lowercaseFirst(stripPrefix(method.getName()));
    }

    private static String lowercaseFirst(String name) {
        if (name.isEmpty()) {
            return name;
        }

        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private static String stripPrefix(String name) {
        return name.startsWith("get")
                ? name.substring(3)
                : name.startsWith("is")
                    ? name.substring(2)
                    : name;
    }
}
