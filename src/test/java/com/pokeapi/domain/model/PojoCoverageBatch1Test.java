package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PojoCoverageBatch1Test {

    @Test
    void itemAndAbilityDetailToStringAndGetters() {
        var item = Item.builder().name("nm").url("u").build();
        assertEquals("nm", item.getName());
        assertEquals("u", item.getUrl());

        var ad = AbilityDetail.builder().name("ad").url("u").build();
        assertEquals("ad", ad.getName());
        assertNotNull(ad.toString());
    }
}
