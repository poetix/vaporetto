package com.codepoetics.vaporetto;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BuilderTest {

    @Test
    public void buildsStructure() {
        Person person = Vaporetto.build(Person.class, p -> p
                .with(Person::name, "Arthur Putey")
                .with(Person::age, 42)
                .with(Person::address, a -> a
                    .with(Address::addressLines, "23 Acacia Avenue", "Sunderland")
                    .with(Address::postcode, "VB6 5UX")));

        assertThat(person.name(), equalTo("Arthur Putey"));
        assertThat(person.age(), equalTo(42));
        assertThat(person.address().addressLines(), contains("23 Acacia Avenue", "Sunderland"));
        assertThat(person.address().postcode(), equalTo("VB6 5UX"));
    }

    @Test
    public void equality() {
        Person person1 = Vaporetto.build(Person.class, p -> p
                .with(Person::name, "Arthur Putey")
                .with(Person::age, 42)
                .with(Person::address, a -> a
                        .with(Address::addressLines, "23 Acacia Avenue", "Sunderland")
                        .with(Address::postcode, "VB6 5UX")));

        Person person2 = Vaporetto.build(Person.class, p -> p
                .with(Person::name, "Arthur Putey")
                .with(Person::age, 42)
                .with(Person::address, a -> a
                        .with(Address::addressLines, "23 Acacia Avenue", "Sunderland")
                        .with(Address::postcode, "VB6 5UX")));

        Person person3 = person2.update(p -> p.with(Person::name, "Arthur Daley"));

        assertThat(person1, not(sameInstance(person2)));
        assertThat(person1, equalTo(person2));
        assertThat(person3, not(equalTo(person2)));
    }

    @Test
    public void updatesStructure() {
        Person person = Vaporetto.build(Person.class, p -> p
                .with(Person::name, "Arthur Putey")
                .with(Person::age, 42)
                .with(Person::address, a -> a
                        .with(Address::addressLines, "23 Acacia Avenue", "Sunderland")
                        .with(Address::postcode, "VB6 5UX")));

        Person updatedPerson = person.update(p -> p
                .with(Person::name, "Arthur Daley")
                .with(Person::address, person.address().update(a -> a
                    .with(Address::postcode, "RA8 81T"))));

        // person is unchanged
        assertThat(person.name(), equalTo("Arthur Putey"));
        assertThat(person.age(), equalTo(42));
        assertThat(person.address().addressLines(), contains("23 Acacia Avenue", "Sunderland"));
        assertThat(person.address().postcode(), equalTo("VB6 5UX"));

        // updated person has new values
        assertThat(updatedPerson.name(), equalTo("Arthur Daley"));
        assertThat(updatedPerson.age(), equalTo(42));
        assertThat(updatedPerson.address().addressLines(), contains("23 Acacia Avenue", "Sunderland"));
        assertThat(updatedPerson.address().postcode(), equalTo("RA8 81T"));
    }

}
