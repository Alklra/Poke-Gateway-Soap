package com.pokeapi.infrastructure.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class DtoWrappersTest {

    PokemonResponse resp;

    @BeforeEach
    void setup() {
        resp = new PokemonResponse();
    }

    @Test
    void pokemonResponseBasicFields() {
        resp.setId(3);
        resp.setName("x");
        resp.setBaseExperience(10);
        assertEquals(3, resp.getId());
        assertEquals("x", resp.getName());
    }

    @Test
    void heldItemWrapperMapping() {
        HeldItemWrapper.ItemWrapper iw = new HeldItemWrapper.ItemWrapper();
        iw.setName("i1");
        iw.setUrl("u1");
        HeldItemWrapper hw = new HeldItemWrapper();
        hw.setItem(iw);
        resp.setHeldItems(List.of(hw));
        assertNotNull(resp.getHeldItems());
        assertEquals(1, resp.getHeldItems().size());
    }

    @Test
    void statAndAbilityWrappers() {
        PokemonResponse.Stat s = new PokemonResponse.Stat();
        PokemonResponse.StatWrapper sw = new PokemonResponse.StatWrapper();
        sw.setStat(s);
        PokemonResponse.Ability a = new PokemonResponse.Ability();
        a.setName("abn");
        PokemonResponse.AbilityWrapper aw = new PokemonResponse.AbilityWrapper();
        aw.setAbility(a);
        // DTO exposes abilities list; assert ability wrapper works
        resp.setAbilities(List.of(aw));
        assertNotNull(resp.getAbilities());
    }

    @Test
    void nullAndEmptyCollections() {
        resp.setAbilities(null);
        resp.setHeldItems(null);
        assertTrue(resp.getAbilities() == null || resp.getAbilities().isEmpty());
        assertTrue(resp.getHeldItems() == null || resp.getHeldItems().isEmpty());
    }

    @Test
    void abilityWrapperToStringNotEmpty() {
        PokemonResponse.AbilityWrapper aw = new PokemonResponse.AbilityWrapper();
        assertNotNull(aw.toString());
    }

    @Test
    void statWrapperToStringNotEmpty() {
        PokemonResponse.StatWrapper sw = new PokemonResponse.StatWrapper();
        assertNotNull(sw.toString());
    }
}
