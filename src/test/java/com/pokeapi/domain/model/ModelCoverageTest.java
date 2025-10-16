package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class ModelCoverageTest {

    @Test
    void pokemonBuildersAndGetters() {
        var item = Item.builder().name("i").url("u").build();
        assertEquals("i", item.getName());
        assertEquals("u", item.getUrl());

        var held = HeldItem.builder().item(item).build();
        assertNotNull(held.getItem());

        var abilityDetail = AbilityDetail.builder().name("a").url("au").build();
        assertEquals("a", abilityDetail.getName());

    var ability = Ability.builder().abilityDetail(abilityDetail).hidden(true).slot(1).build();
    assertTrue(ability.isHidden());
    assertEquals(abilityDetail, ability.getAbilityDetail());

        var p = Pokemon.builder()
                .id(100L)
                .name("p")
                .abilities(List.of("a1", "a2"))
                .baseExperience(50)
                .heldItems(List.of(held))
                .locationAreaEncounters("loc")
                .build();

        assertEquals(100L, p.getId());
        assertEquals("p", p.getName());
        assertEquals(2, p.getAbilities().size());
        assertEquals(50, p.getBaseExperience());
        assertEquals("loc", p.getLocationAreaEncounters());

        // equals/hashCode/toString basic sanity
        assertTrue(p.toString().contains("p"));
        assertNotNull(p.hashCode());
    }
}
