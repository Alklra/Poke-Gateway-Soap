package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AbilityTest {

    @Test
    void abilityBuilderAndAccessors() {
        AbilityDetail ad = AbilityDetail.builder().name("n").url("u").build();
        Ability a = Ability.builder().abilityDetail(ad).hidden(true).slot(2).build();
        assertTrue(a.isHidden());
        assertEquals(2, a.getSlot());
        assertEquals(ad, a.getAbilityDetail());
    }
}
