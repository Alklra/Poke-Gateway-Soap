package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HeldItemTest {

    @Test
    void heldItemBuilderAndGetters() {
        Item item = Item.builder().name("i").url("u").build();
        HeldItem h = HeldItem.builder().item(item).build();
        assertNotNull(h.getItem());
        assertEquals("i", h.getItem().getName());
    }
}
