package com.pokeapi.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PokemonRestClientFetchTest {

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
    void fetchLocationAreasParsesArray() throws Exception {
        String json = "[ { \"location_area\": { \"name\": \"area1\", \"url\": \"http://a1\" } }, { \"location_area\": { \"name\": \"area2\", \"url\": \"http://a2\" } } ]";
        JsonNode node = mapper.readTree(json);
        String url = "https://pokeapi.co/api/v2/pokemon/1/encounters";
        when(restTemplate.getForObject(eq(url), eq(JsonNode.class))).thenReturn(node);

        var out = client.fetchLocationAreas(url);
        assertNotNull(out);
        assertEquals(2, out.size());
        assertEquals("area1", out.get(0).getName());
        assertEquals("http://a2", out.get(1).getUrl());
    }

    @Test
    void fetchLocationAreasNonArrayReturnsEmpty() throws Exception {
        String json = "{ \"not\": \"an array\" }";
        JsonNode node = mapper.readTree(json);
        String url = "https://pokeapi.co/api/v2/pokemon/1/encounters";
        when(restTemplate.getForObject(eq(url), eq(JsonNode.class))).thenReturn(node);

        var out = client.fetchLocationAreas(url);
        assertNotNull(out);
        assertTrue(out.isEmpty());
    }

    @Test
    void fetchLocationAreasNullOrBlankUrlReturnsEmpty() {
        var out1 = client.fetchLocationAreas(null);
        var out2 = client.fetchLocationAreas("");
        assertNotNull(out1);
        assertNotNull(out2);
        assertTrue(out1.isEmpty());
        assertTrue(out2.isEmpty());
    }
}
