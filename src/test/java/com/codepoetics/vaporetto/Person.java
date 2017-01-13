package com.codepoetics.vaporetto;

import java.util.Set;

public interface Person extends VType<Person> {
    String name();
    int age();
    Address address();
    Set<String> phoneNumbers();
}
