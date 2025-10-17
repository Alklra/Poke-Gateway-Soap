package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DomainModelCoverageTest {

    @Test
    void abilityAndDetailBuildersAndEquals() {
        AbilityDetail ad = AbilityDetail.builder().name("a").url("u").build();
        AbilityDetail ad2 = new AbilityDetail("a","u");
        assertEquals(ad, ad2);
        assertEquals(ad.hashCode(), ad2.hashCode());

        Ability a = Ability.builder().abilityDetail(ad).hidden(true).slot(1).build();
        assertTrue(a.isHidden());
        assertEquals(1, a.getSlot());
        assertEquals(ad, a.getAbilityDetail());
    }

    @Test
    void itemAndHeldItemBuilders() {
        Item it = Item.builder().name("it").url("u").build();
        HeldItem hi = HeldItem.builder().item(it).build();
        assertEquals("it", hi.getItem().getName());
    }

    @Test
    void locationAreaDtoEqualsAndToString() {
        LocationAreaDto l1 = new LocationAreaDto("loc","http://l");
        LocationAreaDto l2 = new LocationAreaDto("loc","http://l");
        assertEquals(l1, l2);
        assertTrue(l1.toString().contains("loc"));
    }

    @Test
    void pokemonBuilderGetters() {
        Item it = Item.builder().name("i").url("u").build();
        HeldItem hi = HeldItem.builder().item(it).build();
        Pokemon p = Pokemon.builder()
                .id(1L)
                .name("p")
                .abilities(List.of("a1","a2"))
                .baseExperience(10)
                .heldItems(List.of(hi))
                .locationAreaEncounters("/enc")
                .build();
        assertEquals(1L, p.getId());
        assertEquals("p", p.getName());
        assertEquals(2, p.getAbilities().size());
        assertEquals(10, p.getBaseExperience());
        assertEquals(1, p.getHeldItems().size());
    }
}
