package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HeldItemWrapperTest {

    @Test
    void itemWrapperAccessors() {
        HeldItemWrapper.ItemWrapper iw = new HeldItemWrapper.ItemWrapper();
        iw.setName("i");
        iw.setUrl("u");
        // ensure fields exist on the wrapper
        assertEquals("i", iw.getName());
    }
}
