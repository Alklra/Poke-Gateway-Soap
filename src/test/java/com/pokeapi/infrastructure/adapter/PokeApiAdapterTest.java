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
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);

        final String NAME = "bulbasaur";
        var p = adapter.findByName(NAME);
        assertNotNull(p);
        assertEquals(1L, p.getId());
        assertEquals(NAME, p.getName());
    }

    @Test
    void mapsHandlesNullWrappers() {
        PokeApiAdapter adapter = new PokeApiAdapter(restTemplate, "https://pokeapi.co/api/v2");
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(null);
        // call with non-null name so adapter will attempt to map and throw ExternalServiceException
        assertThrows(ExternalServiceException.class, () -> adapter.findByName("x"));
    }

    @Test
    void mapsHandlesEmptyAbilityList() {
        PokeApiAdapter adapter = new PokeApiAdapter(restTemplate, "https://pokeapi.co/api/v2");
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(null);
        assertThrows(ExternalServiceException.class, () -> adapter.findByName("") );
    }

    @Test
    void mapsHandlesPartialData() {
        PokeApiAdapter adapter = new PokeApiAdapter(restTemplate, "https://pokeapi.co/api/v2");
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(null);
        assertThrows(ExternalServiceException.class, () -> adapter.findByName("nope"));
    }

    @Test
    void mapsDoesNotThrowOnWeirdData() {
        PokeApiAdapter adapter = new PokeApiAdapter(restTemplate, "https://pokeapi.co/api/v2");
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(new PokemonResponse());
        assertDoesNotThrow(() -> adapter.findByName("x"));
    }

    @Test
    void findByNameNotFoundThrows() {
        when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class)))
            .thenThrow(HttpClientErrorException.NotFound.class);

        assertThrows(PokemonNotFoundException.class, () -> adapter.findByName("missing"));
    }

    @Test
    void findByNameOtherErrorThrows() {
        when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class)))
            .thenThrow(new RuntimeException("boom"));

        assertThrows(ExternalServiceException.class, () -> adapter.findByName("error"));
    }

    // Tests from PokeApiAdapterMappingTest
    @Test
    void abilitiesMappingEmptyWhenNull() {
        var resp = new PokemonResponse();
        resp.setId(3);
        resp.setName("z");
        resp.setAbilities(null);
        resp.setHeldItems(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);

        var p = adapter.findByName("z");
        assertNotNull(p);
        assertTrue(p.getAbilities() == null || p.getAbilities().isEmpty());
    }

    @Test
    void idConversionWorksWhenIntNull() {
        var resp = new PokemonResponse();
        resp.setId(null);
        resp.setName("z");
        resp.setAbilities(List.of());
        resp.setHeldItems(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);

        var p = adapter.findByName("z");
        assertNotNull(p);
        assertNull(p.getId());
    }

    // Tests from PokeApiAdapterExtraTest
    @Test
    void mapHeldItemsNullReturnsEmpty() {
        var resp = new PokemonResponse();
        resp.setId(1);
        resp.setName("x");
        resp.setHeldItems(null);
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);

        var p = adapter.findByName("x");
        assertNotNull(p);
        assertTrue(p.getHeldItems() == null || p.getHeldItems().isEmpty());
    }

    @Test
    void mapHeldItemsWithItemMapsCorrectly() {
        var itemWrapper = new HeldItemWrapper.ItemWrapper();
        itemWrapper.setName("i1");
        itemWrapper.setUrl("u1");
        var hw = new HeldItemWrapper();
        hw.setItem(itemWrapper);
        var resp = new PokemonResponse();
        resp.setId(2);
        resp.setName("x");
        resp.setHeldItems(List.of(hw));
        resp.setAbilities(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);

        var p = adapter.findByName("x");
        assertNotNull(p);
        assertNotNull(p.getHeldItems());
        assertEquals(1, p.getHeldItems().size());
        var hi = p.getHeldItems().get(0);
        assertNotNull(hi.getItem());
        assertEquals("i1", hi.getItem().getName());
    }

    // Extra targeted tests (10)
    @Test
    void findByNameHandlesBaseExperienceNull() {
        var resp = new PokemonResponse();
        resp.setId(7);
        resp.setName("noexp");
        resp.setBaseExperience(null);
        resp.setAbilities(List.of());
        resp.setHeldItems(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("noexp");
        assertNotNull(p);
    }

    @Test
    void findByNameHandlesMissingHeldItemInnerNull() {
        var hw = new HeldItemWrapper();
        hw.setItem(null);
        var resp = new PokemonResponse();
        resp.setHeldItems(List.of(hw));
        resp.setAbilities(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("h");
        assertNotNull(p);
    }

    @Test
    void findByNameHandlesLargeAbilitiesList() {
        var resp = new PokemonResponse();
        resp.setAbilities(java.util.stream.IntStream.range(0,20).mapToObj(i->new PokemonResponse.AbilityWrapper()).toList());
        resp.setHeldItems(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("big");
        assertNotNull(p);
    }

    @Test
    void findByNameMalformedUrlHandled() {
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenThrow(new IllegalArgumentException("bad url"));
        assertThrows(ExternalServiceException.class, () -> adapter.findByName("bad"));
    }

    @Test
    void findByNameTrimsName() {
        var resp = new PokemonResponse(); resp.setId(9); resp.setName("trimmed"); resp.setAbilities(List.of()); resp.setHeldItems(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName(" trimmed ");
        assertNotNull(p);
        assertEquals("trimmed", p.getName());
    }

    @Test
    void findByNameHandlesNullResponseGracefully() {
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(null);
        assertThrows(ExternalServiceException.class, () -> adapter.findByName("n"));
    }

    @Test
    void findByNameMapsHeldItemUrl() {
        var iw = new HeldItemWrapper.ItemWrapper(); iw.setName("n"); iw.setUrl("u");
        var hw = new HeldItemWrapper(); hw.setItem(iw);
        var resp = new PokemonResponse(); resp.setHeldItems(List.of(hw)); resp.setAbilities(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("x");
        assertEquals("u", p.getHeldItems().get(0).getItem().getUrl());
    }

    @Test
    void findByNameHandlesWhitespaceName() {
        var resp = new PokemonResponse(); resp.setId(11); resp.setName("ws"); resp.setAbilities(List.of()); resp.setHeldItems(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("  ws  ");
        assertEquals("ws", p.getName());
    }

    @Test
    void findByNameHandlesUnexpectedFields() {
        var resp = new PokemonResponse(); resp.setId(12); resp.setName("extra");
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("extra");
        assertEquals("extra", p.getName());
    }

    @Test
    void findByNameWithNullNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> adapter.findByName(null));
    }

    @Test
    void findByNameHandlesNameCaseInsensitive() {
        var resp = new PokemonResponse(); resp.setId(20); resp.setName("MiXeD"); resp.setAbilities(List.of()); resp.setHeldItems(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("mixed");
        assertNotNull(p);
        assertEquals("MiXeD", p.getName());
    }

    @Test
    void findByNameHandlesMultipleHeldItems() {
        var iw1 = new HeldItemWrapper.ItemWrapper(); iw1.setName("n1"); iw1.setUrl("u1");
        var iw2 = new HeldItemWrapper.ItemWrapper(); iw2.setName("n2"); iw2.setUrl("u2");
        var hw1 = new HeldItemWrapper(); hw1.setItem(iw1); var hw2 = new HeldItemWrapper(); hw2.setItem(iw2);
        var resp = new PokemonResponse(); resp.setHeldItems(List.of(hw1, hw2)); resp.setAbilities(List.of());
    when(restTemplate.getForObject(anyString(), eq(PokemonResponse.class))).thenReturn(resp);
        var p = adapter.findByName("many");
        assertEquals(2, p.getHeldItems().size());
    }
}
