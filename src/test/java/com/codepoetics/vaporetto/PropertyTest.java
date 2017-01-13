package com.codepoetics.vaporetto;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PropertyTest {

    @Test
    public void propertiesCompose() {
        Person person = Vaporetto.build(Person.class, p -> p
                .with(Person::name, "Arthur Putey")
                .with(Person::age, 42)
                .with(Person::address, a -> a
                        .with(Address::addressLines, "23 Acacia Avenue", "Sunderland")
                        .with(Address::postcode, "VB6 5UX")));

        Property<Person, Address> address = Property.of(Person.class, Person::address);
        Property<Address, String> postcode = Property.of(Address.class, Address::postcode);
        Property<Person, String> addressPostcode = Property.of(
                Property.of(Person.class, Person::address),
                Address::postcode);

        assertThat(addressPostcode.getName(), equalTo("address.postcode"));
        assertThat(addressPostcode.get(person), equalTo("VB6 5UX"));

        assertThat(addressPostcode.update(person, "RA8 81T"),
                equalTo(Vaporetto.build(Person.class, p -> p
                    .with(Person::name, "Arthur Putey")
                    .with(Person::age, 42)
                    .with(Person::address, a -> a
                            .with(Address::addressLines, "23 Acacia Avenue", "Sunderland")
                            .with(Address::postcode, "RA8 81T")))));
    }
}
