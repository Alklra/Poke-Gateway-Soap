package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class PokemonSimpleTest {

    @Test
    void pokemonBasic() {
        Pokemon p = Pokemon.builder().id(1L).name("bulbasaur").abilities(List.of("a")).build();
        assertEquals(1L, p.getId());
        assertEquals("bulbasaur", p.getName());
        assertEquals(1, p.getAbilities().size());
    }
}
