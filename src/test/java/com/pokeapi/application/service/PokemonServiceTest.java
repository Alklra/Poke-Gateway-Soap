package com.pokeapi.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.port.PokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

class PokemonServiceTest {

    @Mock
    private PokemonRepository repository;

    private PokemonService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PokemonService(repository);
    }

    @Test
    void getIdReturnsId() {
        final String NAME = "bulbasaur";
        var p = Pokemon.builder().id(42L).name(NAME).build();
        when(repository.findByName(NAME)).thenReturn(p);

        var id = service.getId(NAME);
        assertEquals(42L, id);
    }

    @Test
    void getAbilitiesReturnsList() {
        final String NAME = "p";
        var p = Pokemon.builder().id(1L).name(NAME).abilities(List.of("a","b")).build();
        when(repository.findByName(NAME)).thenReturn(p);

        var abilities = service.getAbilities(NAME);
        assertEquals(2, abilities.size());
        assertEquals("a", abilities.get(0));
    }

    @Test
    void getHeldItemsReturnsList() {
        final String NAME = "p";
        var item = HeldItem.builder().item(null).build();
        var p = Pokemon.builder().id(1L).name(NAME).heldItems(List.of(item)).build();
        when(repository.findByName(NAME)).thenReturn(p);

        var held = service.getHeldItems(NAME);
        assertNotNull(held);
        assertEquals(1, held.size());
    }
}
