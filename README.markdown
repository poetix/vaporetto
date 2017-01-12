Vaporetto
=========

Very lightweight proxy-based immutable value types for Java 8.

Define types as follows:

```java
public interface Address extends VType<Address> {
    Collection<String> addressLines();
    String postcode();
}

public interface Person extends VType<Person> {
    String name();
    int age();
    Address address();
}
```

Build values like this:

```java
Person person = Vaporetto.build(Person.class, p -> p
    .with(Person::name, "Arthur Putey")
    .with(Person::age, 42)
    .with(Person::address, a -> a
        .with(Address::addressLines, "23 Acacia Avenue", "Sunderland")
        .with(Address::postcode, "VB6 5UX")));
```

Update values like this:

```java
Person updated = person.with(p -> p
    .with(Person::age, 43));
```
