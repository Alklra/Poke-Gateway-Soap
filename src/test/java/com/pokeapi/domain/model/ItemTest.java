package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void itemBuilder() {
        Item it = Item.builder().name("name").url("url").build();
        assertEquals("name", it.getName());
        assertEquals("url", it.getUrl());
    }
}
