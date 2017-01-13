package com.codepoetics.vaporetto;

import java.util.Collection;

public interface Address extends VType<Address> {
    Collection<String> addressLines();
    String postcode();
}
