package com.codepoetics.vaporetto;

import java.util.Map;
import java.util.function.UnaryOperator;

public interface VType<T> {
    T with(UnaryOperator<Vaporetto<T>> builder);
    Map<String, Object> getProperties();
}
