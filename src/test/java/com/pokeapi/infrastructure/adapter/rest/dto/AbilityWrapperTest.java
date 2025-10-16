package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AbilityWrapperTest {

    @Test
    void abilityWrapperBasic() {
        PokemonResponse.AbilityWrapper aw = new PokemonResponse.AbilityWrapper();
        PokemonResponse.Ability a = new PokemonResponse.Ability();
        a.setName("abilityName");
        aw.setAbility(a);
        assertEquals("abilityName", aw.getAbility().getName());
    }
}
