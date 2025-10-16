package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PokemonResponseMappingTest {

    @Test
    void pokemonResponseBasicAccessors() {
        PokemonResponse p = new PokemonResponse();
        p.setName("pikachu");
        p.setBaseExperience(112);
        assertEquals("pikachu", p.getName());
        assertEquals(112, p.getBaseExperience());
    }
}
