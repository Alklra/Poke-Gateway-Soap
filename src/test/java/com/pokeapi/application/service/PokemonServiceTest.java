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

    // from PokemonServiceExtraTest - simple sanity cases
    @Test
    void getIdWhenRepositoryNullReturnsNull() {
        when(repository.findByName(anyString())).thenReturn(null);
        var id = service.getId("nope");
        assertNull(id);
    }

    @Test
    void getAbilitiesWhenRepositoryNullReturnsEmpty() {
        when(repository.findByName(anyString())).thenReturn(null);
        var abilities = service.getAbilities("nope");
        assertTrue(abilities == null || abilities.isEmpty());
    }

    // Extra targeted tests (7)
    @Test
    void getIdWhenRepositoryThrowsHandled() {
        when(repository.findByName(anyString())).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> service.getId("x"));
    }

    @Test
    void getAbilitiesWhenEmptyList() {
        var p = Pokemon.builder().id(1L).name("n").abilities(List.of()).build();
        when(repository.findByName("n")).thenReturn(p);
        var a = service.getAbilities("n");
        assertTrue(a.isEmpty());
    }

    @Test
    void getHeldItemsWhenRepositoryThrows() {
        when(repository.findByName(anyString())).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> service.getHeldItems("x"));
    }

    @Test
    void getHeldItemsWhenEmpty() {
        var p = Pokemon.builder().id(1L).name("n").heldItems(List.of()).build();
        when(repository.findByName("n")).thenReturn(p);
        var h = service.getHeldItems("n");
        assertTrue(h.isEmpty());
    }

    @Test
    void getAbilitiesNullNameHandled() {
        assertThrows(IllegalArgumentException.class, () -> service.getAbilities(null));
    }

    @Test
    void getHeldItemsNullNameHandled() {
        assertThrows(IllegalArgumentException.class, () -> service.getHeldItems(null));
    }

    @Test
    void getIdNullNameHandled() {
        assertThrows(IllegalArgumentException.class, () -> service.getId(null));
    }

    @Test
    void getNameReturnsValue() {
        var p = Pokemon.builder().id(2L).name("nm").build();
        when(repository.findByName("nm")).thenReturn(p);
        assertEquals("nm", service.getName("nm"));
    }

    @Test
    void getBaseExperienceReturnsInt() {
        var p = Pokemon.builder().id(3L).name("be").baseExperience(7).build();
        when(repository.findByName("be")).thenReturn(p);
        assertEquals(7, service.getBaseExperience("be"));
    }
}
