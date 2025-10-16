package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PokemonResponseAbilityTest {

    @Test
    void nestedAbility() {
        PokemonResponse.AbilityWrapper aw = new PokemonResponse.AbilityWrapper();
        PokemonResponse.Ability a = new PokemonResponse.Ability();
        a.setName("n");
        aw.setAbility(a);
        assertEquals("n", aw.getAbility().getName());
    }
}
