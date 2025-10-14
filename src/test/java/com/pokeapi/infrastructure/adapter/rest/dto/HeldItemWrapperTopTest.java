package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class HeldItemWrapperTopTest {

    @Test
    void heldItemsList() {
        HeldItemWrapper.ItemWrapper iw = new HeldItemWrapper.ItemWrapper();
        iw.setName("n");
        iw.setUrl("u");
        // ensure wrapper fields are accessible
        assertEquals("n", iw.getName());
        assertEquals("u", iw.getUrl());
    }
}
