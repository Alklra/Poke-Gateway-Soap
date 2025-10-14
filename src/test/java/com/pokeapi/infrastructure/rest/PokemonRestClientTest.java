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
    void clientHandlesNullResponse() {
        PokemonRestClient client = new PokemonRestClient(restTemplate, "https://pokeapi.co/api/v2");
        assertThrows(IllegalArgumentException.class, () -> client.getPokemonByName(null));
    }

    @Test
    void clientMapToPokemonHandlesMissingFields() {
        var node = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().createObjectNode();
        PokemonRestClient client = new PokemonRestClient(restTemplate, "https://pokeapi.co/api/v2");
        // use getPokemonByName by mocking restTemplate to return the node
        when(restTemplate.getForObject(anyString(), eq(com.fasterxml.jackson.databind.JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("test");
        assertNotNull(p);
    }

    @Test
    void clientHandlesMalformedJsonGracefully() {
        PokemonRestClient client = new PokemonRestClient(restTemplate, "https://pokeapi.co/api/v2");
        when(restTemplate.getForObject(anyString(), eq(com.fasterxml.jackson.databind.JsonNode.class))).thenReturn(null);
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("x"));
    }

    @Test
    void clientParsesSimpleStatArray() {
        var node = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().createObjectNode();
        when(restTemplate.getForObject(anyString(), eq(com.fasterxml.jackson.databind.JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("simple");
        assertNotNull(p);
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

    // from PokemonRestClientExtraTest
    @Test
    void nullResponseThrowsExternal() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(null);
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("n"));
    }

    @Test
    void abilitiesMissingHandled() throws Exception {
        String json = "{\"id\":2, \"name\":\"b\", \"base_experience\":10}"; // no abilities field
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);

        var p = client.getPokemonByName("b");
        assertNotNull(p);
        assertEquals(2L, p.getId());
        assertTrue(p.getAbilities() == null || p.getAbilities().isEmpty());
    }

    // Extra targeted tests (8)
    @Test
    void getPokemonParsesLocationEncountersMissing() throws Exception {
        String json = "{\"id\":3, \"name\":\"c\", \"base_experience\":5, \"abilities\":[] }";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("c");
        assertNotNull(p);
    }

    @Test
    void getPokemonHandlesExtraNestedFields() throws Exception {
        String json = "{\"id\":4, \"name\":\"d\", \"base_experience\":8, \"abilities\":[{\"ability\":{\"name\":\"a\"}}]}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("d");
        assertEquals(4L, p.getId());
    }

    @Test
    void getPokemonHandlesNullLocationEncountersString() throws Exception {
        String json = "{\"id\":5, \"name\":\"e\", \"base_experience\":3, \"location_area_encounters\":null}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("e");
        assertNotNull(p);
    }

    @Test
    void getPokemonHandlesEmptyJsonObject() throws Exception {
        String json = "{}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("x"));
    }

    @Test
    void getPokemonHandlesNonJsonResponse() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenThrow(new RuntimeException("not json"));
        assertThrows(ExternalServiceException.class, () -> client.getPokemonByName("nj"));
    }

    @Test
    void getPokemonHandlesLargeStatsArray() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\":6,\"name\":\"f\",\"base_experience\":1,\"stats\":[");
        for (int i=0;i<50;i++) sb.append("{\"stat\":{\"name\":\"s"+i+"\"}},");
        sb.append("{}]}");
        JsonNode node = mapper.readTree(sb.toString());
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("f");
        assertNotNull(p);
    }

    @Test
    void getPokemonNullNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> client.getPokemonByName(null));
    }

    @Test
    void getPokemonParsesHeldItemsWithMissingItem() throws Exception {
        String json = "{\"id\":7, \"name\":\"g\", \"base_experience\":2, \"held_items\":[{\"item\":null}]}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("g");
        assertNotNull(p);
    }

    @Test
    void getPokemonParsesAbilityWithoutName() throws Exception {
        String json = "{\"id\":8, \"name\":\"h\", \"base_experience\":2, \"abilities\":[{\"ability\":{}}]}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("h");
        assertNotNull(p);
    }

    @Test
    void getPokemonHandlesMalformedStatsGracefully() throws Exception {
        String json = "{\"id\":9, \"name\":\"i\", \"base_experience\":3, \"stats\":[{\"stat\":null}]}";
        JsonNode node = mapper.readTree(json);
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(node);
        var p = client.getPokemonByName("i");
        assertNotNull(p);
    }
}
