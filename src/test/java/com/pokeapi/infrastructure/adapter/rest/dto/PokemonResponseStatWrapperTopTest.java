package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class PokemonResponseStatWrapperTopTest {

    @Test
    void statsList() {
        PokemonResponse.StatWrapper sw = new PokemonResponse.StatWrapper();
        PokemonResponse.Stat s = new PokemonResponse.Stat();
        // StatWrapper holds baseStat and stat; assert wrapper behavior
        sw.setBaseStat(3);
        sw.setStat(s);
        assertEquals(3, sw.getBaseStat());
    }
}
