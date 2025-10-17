package com.pokeapi.application.service;

import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.model.Item;
import com.pokeapi.domain.model.LocationAreaDto;
import com.pokeapi.domain.port.PokemonRepository;
import com.pokeapi.infrastructure.rest.PokemonRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PokemonServiceTest {

    private PokemonRestClient mockClient;
    private PokemonRepository mockRepo;
    private PokemonService service;

    @BeforeEach
    public void setup() {
        mockRepo = Mockito.mock(PokemonRepository.class);
        mockClient = Mockito.mock(PokemonRestClient.class);
        service = new PokemonService(mockRepo, mockClient);
    }

    @Test
    public void getPokemonHappyPath_returnsMappedPokemon() {
        var p = Pokemon.builder()
            .id(25L)
            .name("pikachu")
            .baseExperience(112)
            .abilities(List.of("static"))
            .heldItems(List.of(HeldItem.builder().item(Item.builder().name("light-ball").url("http://item").build()).build()))
            .locationAreaEncounters("https://pokeapi.co/api/v2/pokemon/25/encounters")
            .build();
        when(mockRepo.findByName("pikachu")).thenReturn(p);
        when(mockClient.fetchLocationAreas(p.getLocationAreaEncounters()))
                .thenReturn(List.of(new LocationAreaDto("location-area","http://loc")));

        assertEquals(25L, service.getId("pikachu"));
        assertEquals("pikachu", service.getName("pikachu"));
        assertEquals(Integer.valueOf(112), service.getBaseExperience("pikachu"));
        assertFalse(service.getHeldItems("pikachu").isEmpty());
        assertEquals("https://pokeapi.co/api/v2/pokemon/25/encounters", service.getLocationAreaEncounters("pikachu"));

        var areas = service.getLocationAreas("pikachu");
        assertNotNull(areas);
        assertFalse(areas.isEmpty());
    verify(mockRepo, atLeastOnce()).findByName("pikachu");
        verify(mockClient, times(1)).fetchLocationAreas(p.getLocationAreaEncounters());
    }

    @Test
    public void unknownName_returnsNullsAndEmptyCollections() {
        when(mockRepo.findByName("unknown")).thenReturn(null);
        when(mockClient.fetchLocationAreas(null)).thenReturn(List.of());

        assertNull(service.getId("unknown"));
        assertNull(service.getName("unknown"));
        assertNull(service.getBaseExperience("unknown"));
        assertTrue(service.getAbilities("unknown").isEmpty());
        assertTrue(service.getHeldItems("unknown").isEmpty());
        assertTrue(service.getLocationAreas("unknown").isEmpty());
    verify(mockRepo, atLeastOnce()).findByName("unknown");
    }
}
