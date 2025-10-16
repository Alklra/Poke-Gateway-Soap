package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StatWrapperTest {

    @Test
    void statAccessors() {
        PokemonResponse.Stat s = new PokemonResponse.Stat();
        // Stat in DTO only has name property; baseStat exists on StatWrapper
        PokemonResponse.StatWrapper sw = new PokemonResponse.StatWrapper();
        sw.setBaseStat(10);
        sw.setStat(s);
        assertEquals(10, sw.getBaseStat());
    }
}
