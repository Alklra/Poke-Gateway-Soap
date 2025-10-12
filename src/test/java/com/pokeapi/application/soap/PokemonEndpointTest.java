package com.pokeapi.application.soap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pokeapi.application.soap.generated.GetIdRequest;
import com.pokeapi.application.soap.generated.GetIdResponse;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import com.pokeapi.domain.service.PokemonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

class PokemonEndpointTest {

    @Mock
    private PokemonService service;
    @Mock
    private MessageContext messageContext;
    @Mock
    private SaajSoapMessage saaj;

    private PokemonEndpoint endpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        endpoint = new PokemonEndpoint(service);
    }

    @Test
    void getIdHappy() {
        final String NAME = "bulbasaur";
        when(service.getId(NAME)).thenReturn(5L);
        var req = new GetIdRequest();
        req.setName(NAME);
        var resp = endpoint.getId(req, messageContext);
        assertNotNull(resp);
        assertEquals(5L, resp.getId());
    }

    @Test
    void getIdNotFoundBuildsFault() {
        final String NAME = "missing";
        when(service.getId(NAME)).thenThrow(new PokemonNotFoundException(NAME));
        when(messageContext.getResponse()).thenReturn(saaj);
        var req = new GetIdRequest();
        req.setName(NAME);
        var resp = endpoint.getId(req, messageContext);
        assertNull(resp);
    }
}
