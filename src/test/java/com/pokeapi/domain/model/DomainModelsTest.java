package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class DomainModelsTest {

    Pokemon p;

    @BeforeEach
    void setup() {
        p = Pokemon.builder().id(10L).name("p").baseExperience(5).build();
    }

    @Test
    void pokemonBuilderAndGetters() {
        assertEquals(10L, p.getId());
        assertEquals("p", p.getName());
        assertEquals(5, p.getBaseExperience());
    }

    @Test
    void itemBuilderAndFields() {
        Item it = Item.builder().name("i").url("u").build();
        assertEquals("i", it.getName());
        assertEquals("u", it.getUrl());
    }

    @Test
    void abilityAndDetailBuilders() {
        AbilityDetail ad = AbilityDetail.builder().name("an").url("au").build();
        Ability ab = Ability.builder().abilityDetail(ad).hidden(true).slot(2).build();
        assertNotNull(ab.getAbilityDetail());
        assertEquals("an", ab.getAbilityDetail().getName());
        assertTrue(ab.isHidden());
        assertEquals(2, ab.getSlot());
    }

    @Test
    void heldItemBuilder() {
        Item it = Item.builder().name("i").url("u").build();
        HeldItem hi = HeldItem.builder().item(it).build();
        assertNotNull(hi.getItem());
        assertEquals("i", hi.getItem().getName());
    }

    @Test
    void pokemonNullCollectionsHandled() {
        Pokemon p2 = Pokemon.builder().id(5L).name("n").abilities(null).heldItems(null).build();
        assertNotNull(p2);
        assertTrue(p2.getAbilities() == null || p2.getAbilities().isEmpty());
        assertTrue(p2.getHeldItems() == null || p2.getHeldItems().isEmpty());
    }

    @Test
    void pokemonEqualsAndHashCode() {
        Pokemon a = Pokemon.builder().id(1L).name("x").build();
        Pokemon b = Pokemon.builder().id(1L).name("x").build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void itemToStringNotEmpty() {
        Item it = Item.builder().name("i").url("u").build();
        assertNotNull(it.toString());
    }
}
