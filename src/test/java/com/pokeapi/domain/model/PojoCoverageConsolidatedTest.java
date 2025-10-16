package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PojoCoverageConsolidatedTest {

    @Test
    void basicPojoChecks() {
        Item it = Item.builder().name("i").url("u").build();
        assertEquals("i", it.getName());
        assertEquals("u", it.getUrl());
        assertNotNull(it.toString());

        AbilityDetail ad = AbilityDetail.builder().name("a").url("au").build();
        assertEquals("a", ad.getName());
        assertNotNull(ad.toString());

        Ability a = Ability.builder().abilityDetail(ad).hidden(true).slot(1).build();
        assertTrue(a.isHidden());
        assertEquals(ad, a.getAbilityDetail());

        HeldItem hi = HeldItem.builder().item(it).build();
        assertNotNull(hi.getItem());

        Pokemon p = Pokemon.builder().id(1L).name("p").baseExperience(10).build();
        assertEquals(1L, p.getId());
        assertNotNull(p.toString());
    }
}
