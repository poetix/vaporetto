package com.codepoetics.vaporetto;

public interface Person extends VType<Person> {
    String name();
    int age();
    Address address();
}
