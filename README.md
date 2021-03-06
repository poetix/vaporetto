Vaporetto
=========

[![Build Status](https://travis-ci.org/poetix/vaporetto.svg?branch=master)](https://travis-ci.org/poetix/vaporetto)
[![Maven Central](https://img.shields.io/maven-central/v/com.codepoetics/vaporetto.svg)](http://search.maven.org/#search%7Cga%7C1%vaporetto)

Very lightweight proxy-based immutable value types for Java 8. (Compare [phantom-pojos](https://github.com/poetix/phantom-pojos), an earlier iteration of the same idea).

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
Person updated = person.update(p -> p
    .with(Person::age, 43));
```

Reify properties into lenses like this:

```java
Property<Person, Address> address = Property.of(Person.class, Person::address);
Property<Address, String> postcode = Property.of(Address.class, Address::postcode);
Property<Person, String> addressPostcode = address.chain(postcode);

// OR
Property<Person, String> addressPostcode = Property.of(
    Property.of(Person.class, Person::Address),
    Address::postcode);

assertThat(addressPostcode.get(person), equalTo("VB6 5UX"));

Person updatedPerson = addressPostcode.update(person, "RA8 81T");
assertThat(updatedPerson.address().postcode(), equalTo("RA8 81T"));
```
