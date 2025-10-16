package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AbilityDetailTest {

    @Test
    void abilityDetailBuilder() {
        AbilityDetail ad = AbilityDetail.builder().name("n").url("u").build();
        assertEquals("n", ad.getName());
        assertEquals("u", ad.getUrl());
    }
}
