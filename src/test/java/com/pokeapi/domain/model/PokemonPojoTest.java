package com.pokeapi.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PokemonPojoTest {

    @Test
    public void settersAndGettersWorkAndHashCodeEquals() {
        Pokemon p1 = new Pokemon();
        p1.setId(1L);
        p1.setName("testmon");
        p1.setBaseExperience(50);
        p1.setAbilities(List.of("ability1","ability2"));

        Pokemon p2 = Pokemon.builder()
                .id(1L)
                .name("testmon")
                .baseExperience(50)
                .abilities(List.of("ability1","ability2"))
                .build();

        assertEquals(p1.getId(), p2.getId());
        assertEquals(p1.getName(), p2.getName());
        assertEquals(p1.getBaseExperience(), p2.getBaseExperience());
        assertEquals(p1.getAbilities(), p2.getAbilities());
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(p1, p2);

        // Mutate p2 and ensure inequality
        p2.setName("other");
        assertNotEquals(p1, p2);
    }
}
