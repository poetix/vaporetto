package com.codepoetics.vaporetto;

import java.util.Map;
import java.util.function.UnaryOperator;

public interface VType<T> {
    T update(UnaryOperator<Vaporetto<T>> builder);
    Map<String, Object> getProperties();
}
