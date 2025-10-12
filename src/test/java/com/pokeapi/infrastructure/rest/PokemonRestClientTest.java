package com.pokeapi.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import com.pokeapi.domain.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

class PokemonRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private PokemonRestClient client;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new PokemonRestClient(restTemplate, "https://pokeapi.co/api/v2");
    }

    @Test
    void getPokemonByNameHappyPath() throws Exception {
        String json = "{\"id\":1, \"name\":\"bulbasaur\", \"base_experience\":64, \"abilities\":[], \"held_items\":[], \"location_area_encounters\":\"/encounters\"}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);

        final String NAME = "bulbasaur";
        var p = client.getPokemonByName(NAME);
        assertNotNull(p);
        assertEquals(1L, p.getId());
        assertEquals(NAME, p.getName());
    }

    @Test
    void getPokemonByNameNotFoundThrows() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenThrow(HttpClientErrorException.NotFound.class);
        assertThrows(PokemonNotFoundException.class, () -> client.getPokemonByName("missing"));
    }

    @Test
    void getPokemonByNameOtherErrorThrows() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenThrow(new RuntimeException("boom"));
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("err"));
    }
}
