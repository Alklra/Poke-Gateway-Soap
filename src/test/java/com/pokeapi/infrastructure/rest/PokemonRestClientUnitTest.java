package com.pokeapi.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pokeapi.domain.exception.ExternalServiceException;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import com.pokeapi.infrastructure.rest.PokemonRestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public class PokemonRestClientUnitTest {

    @Mock
    private RestTemplate restTemplate;

    private PokemonRestClient client;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        client = new PokemonRestClient(restTemplate, "https://pokeapi.co/api/v2");
    }

    @Test
    void getPokemonByNameNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> client.getPokemonByName(null));
    }

    @Test
    void getPokemonByNameResponseNullThrowsExternalService() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(null);
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("x"));
    }

    @Test
    void getPokemonByNameResourceAccessBecomesExternalService() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenThrow(new ResourceAccessException("io"));
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("x"));
    }

    @Test
    void getPokemonByNameEmptyObjectThrowsExternalService() throws Exception {
        JsonNode node = mapper.readTree("{}\n");
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("x"));
    }

    @Test
    void fetchLocationAreasHandles404AndExceptions() throws Exception {
        String url = "https://pokeapi.co/api/v2/pokemon/1/encounters";
    when(restTemplate.getForObject(eq(url), eq(JsonNode.class))).thenThrow(HttpClientErrorException.create(org.springframework.http.HttpStatus.NOT_FOUND, "Not Found", null, null, null));
        var out = client.fetchLocationAreas(url);
        assertNotNull(out);
        assertTrue(out.isEmpty());

        when(restTemplate.getForObject(eq(url), eq(JsonNode.class))).thenThrow(new RuntimeException("boom"));
        assertThrows(ExternalServiceException.class, () -> client.fetchLocationAreas(url));
    }

    @Test
    void mapToPokemonParsesAbilitiesHeldItemsAndBaseExp() throws Exception {
        String json = "{\n" +
                "  \"id\": 10,\n" +
                "  \"name\": \"testmon\",\n" +
                "  \"base_experience\": 77,\n" +
                "  \"abilities\": [ { \"ability\": { \"name\": \"first\" } }, { \"ability\": { \"name\": \"second\" } } ],\n" +
                "  \"held_items\": [ { \"item\": { \"name\": \"i1\", \"url\": \"http://i1\" } }, { \"item\": null } ],\n" +
                "  \"location_area_encounters\": \"https://poke/api/enc\"\n" +
                "}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(contains("/pokemon/"), eq(JsonNode.class))).thenReturn(node);

        var p = client.getPokemonByName("testmon");
        assertNotNull(p);
        assertEquals(10L, p.getId().longValue());
        assertEquals("testmon", p.getName());
        assertEquals(Integer.valueOf(77), p.getBaseExperience());
        assertEquals(2, p.getAbilities().size());
        assertEquals(2, p.getHeldItems().size());
        assertEquals("https://poke/api/enc", p.getLocationAreaEncounters());
    }

}
