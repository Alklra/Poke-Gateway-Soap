package com.pokeapi.infrastructure.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pokeapi.infrastructure.adapter.rest.dto.PokemonResponse;
import com.pokeapi.infrastructure.adapter.rest.dto.HeldItemWrapper;
import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import com.pokeapi.domain.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

class PokeApiAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private PokeApiAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new PokeApiAdapter(restTemplate, "https://pokeapi.co/api/v2");
    }

    @Test
    void findByNameHappyPath() {
        var resp = new PokemonResponse();
        resp.setId(1);
        resp.setName("bulbasaur");
        resp.setBaseExperience(64);
        resp.setAbilities(List.of());
        resp.setHeldItems(List.of());
        when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class), anyString())).thenReturn(resp);

        final String NAME = "bulbasaur";
        var p = adapter.findByName(NAME);
        assertNotNull(p);
        assertEquals(1L, p.getId());
        assertEquals(NAME, p.getName());
    }

    @Test
    void findByNameNotFoundThrows() {
        when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class), anyString()))
            .thenThrow(HttpClientErrorException.NotFound.class);

        assertThrows(PokemonNotFoundException.class, () -> adapter.findByName("missing"));
    }

    @Test
    void findByNameOtherErrorThrowsExternal() {
        when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class), anyString()))
            .thenThrow(new RuntimeException("boom"));

        assertThrows(ExternalServiceException.class, () -> adapter.findByName("error"));
    }
}
