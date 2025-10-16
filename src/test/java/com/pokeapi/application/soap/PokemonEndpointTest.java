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
import com.pokeapi.testutils.TestMessageContext;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

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

    /*
    // from PokemonEndpointUnitTest (duplicated - commented out to avoid repetition)
    @Test
    void getIdHappyPath_unit() {
        var req = new GetIdRequest();
        req.setName("bulbasaur");
        when(service.getId("bulbasaur")).thenReturn(1L);

        var resp = endpoint.getId(req, messageContext);
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
    }

    @Test
    void getIdNotFoundProducesNullAndBuildsFault_unit() {
        var req = new GetIdRequest();
        req.setName("missing");
        when(service.getId("missing")).thenThrow(new PokemonNotFoundException("missing"));

        var resp = endpoint.getId(req, messageContext);
        assertNull(resp);
        verify(service).getId("missing");
    }

    @Test
    void getIdExternalServiceProducesNull_unit() {
        var req = new GetIdRequest();
        req.setName("err");
        when(service.getId("err")).thenThrow(new com.pokeapi.domain.exception.ExternalServiceException("pokeapi","down"));

        var resp = endpoint.getId(req, messageContext);
        assertNull(resp);
        verify(service).getId("err");
    }
    */

    // from PokemonEndpointHeldItemsTest
    @Test
    void getHeldItemsMapsCorrectly_combined() {
        com.pokeapi.domain.model.Item item = com.pokeapi.domain.model.Item.builder().name("i").url("u").build();
        com.pokeapi.domain.model.HeldItem hi = com.pokeapi.domain.model.HeldItem.builder().item(item).build();
        when(service.getHeldItems("bulbasaur")).thenReturn(java.util.List.of(hi));

        var req = new com.pokeapi.application.soap.generated.GetHeldItemsRequest();
        req.setName("bulbasaur");

    var resp = endpoint.getHeldItems(req, null);
    assertNotNull(resp);
    assertNotNull(resp.getHeldItems());
    assertEquals(1, resp.getHeldItems().getHeldItem().size());
    }

    @Test
    void getIdNotFoundBuildsRealSoapFault() throws Exception {
        // Use a real SAAJ message so buildSoapFault actually writes into the body
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage sm = mf.createMessage();
        SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
        factory.afterPropertiesSet();
        SaajSoapMessage ssm = new SaajSoapMessage(sm);

    // Use TestMessageContext with real SaajSoapMessage as response
    TestMessageContext dmc = new TestMessageContext(ssm);

        when(service.getId("missing")).thenThrow(new com.pokeapi.domain.exception.PokemonNotFoundException("missing"));
        var req = new GetIdRequest();
        req.setName("missing");
        var resp = endpoint.getId(req, dmc);
        assertNull(resp);
        // ensure SOAP fault was created in the Saaj message
        var body = ssm.getSaajMessage().getSOAPBody();
        assertTrue(body.hasFault());
    }

    // Additional small tests to cover more branches
    @Test
    void getNameHappy() {
        when(service.getName("x")).thenReturn("XNAME");
        var req = new com.pokeapi.application.soap.generated.GetNameRequest();
        req.setName("x");
        var resp = endpoint.getName(req, null);
        assertNotNull(resp);
    }

    @Test
    void getNameNotFound() {
        when(service.getName("m")).thenThrow(new com.pokeapi.domain.exception.PokemonNotFoundException("m"));
        var req = new com.pokeapi.application.soap.generated.GetNameRequest();
        req.setName("m");
        var resp = endpoint.getName(req, null);
        assertNull(resp);
    }

    @Test
    void getBaseExperienceHappy() {
        when(service.getBaseExperience("b")).thenReturn(12);
        var req = new com.pokeapi.application.soap.generated.GetBaseExperienceRequest();
        req.setName("b");
        var resp = endpoint.getBaseExperience(req, null);
        assertNotNull(resp);
    }

    @Test
    void getBaseExperienceNotFound() {
        when(service.getBaseExperience("n")).thenThrow(new com.pokeapi.domain.exception.PokemonNotFoundException("n"));
        var req = new com.pokeapi.application.soap.generated.GetBaseExperienceRequest();
        req.setName("n");
        var resp = endpoint.getBaseExperience(req, null);
        assertNull(resp);
    }

    @Test
    void getAbilityNamesHappy() {
        when(service.getAbilities("a")).thenReturn(java.util.List.of("one","two"));
        var req = new com.pokeapi.application.soap.generated.GetAbilityNamesRequest();
        req.setName("a");
    var resp = endpoint.getAbilityNames(req, null);
    assertNotNull(resp);
    assertNotNull(resp.getAbilities());
    assertEquals(2, resp.getAbilities().getAbility().size());
    }

    @Test
    void getAbilityNamesEmpty() {
        when(service.getAbilities("e")).thenReturn(java.util.List.of());
        var req = new com.pokeapi.application.soap.generated.GetAbilityNamesRequest();
        req.setName("e");
    var resp = endpoint.getAbilityNames(req, null);
    assertNotNull(resp);
    }

    @Test
    void getLocationAreaEncountersHappy() {
        when(service.getLocationAreaEncounters("l")).thenReturn("/loc");
        var req = new com.pokeapi.application.soap.generated.GetLocationAreaEncountersRequest();
        req.setName("l");
        var resp = endpoint.getLocationAreaEncounters(req, null);
        assertNotNull(resp);
    }

    @Test
    void getLocationAreaEncountersNotFound() {
        when(service.getLocationAreaEncounters("nf")).thenThrow(new com.pokeapi.domain.exception.PokemonNotFoundException("nf"));
        var req = new com.pokeapi.application.soap.generated.GetLocationAreaEncountersRequest();
        req.setName("nf");
        var resp = endpoint.getLocationAreaEncounters(req, null);
        assertNull(resp);
    }

    @Test
    void getHeldItemsEmptyWhenServiceReturnsNull() {
        when(service.getHeldItems("z")).thenReturn(null);
        var req = new com.pokeapi.application.soap.generated.GetHeldItemsRequest();
        req.setName("z");
        var resp = endpoint.getHeldItems(req, null);
        assertNotNull(resp);
    }

    @Test
    void endpointHandlesNullNameGracefully() {
        var req = new com.pokeapi.application.soap.generated.GetIdRequest();
        req.setName(null);
        var resp = endpoint.getId(req, null);
        assertNull(resp);
    }

    @Test
    void endpointHandlesEmptyNameGracefully() {
        var req = new com.pokeapi.application.soap.generated.GetIdRequest();
        req.setName("");
        var resp = endpoint.getId(req, null);
        assertNull(resp);
    }

    @Test
    void buildSoapFaultCalledWhenResponseIsSaaj() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage sm = mf.createMessage();
        SaajSoapMessage ssm = new SaajSoapMessage(sm);
    TestMessageContext dmc = new TestMessageContext(ssm);
        when(service.getId("err")).thenThrow(new com.pokeapi.domain.exception.ExternalServiceException("poke","down"));
        var req = new GetIdRequest(); req.setName("err");
        var resp = endpoint.getId(req, dmc);
        assertNull(resp);
        assertTrue(ssm.getSaajMessage().getSOAPBody().hasFault());
    }

    // New tests to increase coverage (edge cases and null handling)
    @Test
    void getHeldItemsHandlesNullItemInDomain() {
        com.pokeapi.domain.model.HeldItem hi = com.pokeapi.domain.model.HeldItem.builder().item(null).build();
        when(service.getHeldItems("b")).thenReturn(java.util.List.of(hi));
        var req = new com.pokeapi.application.soap.generated.GetHeldItemsRequest();
        req.setName("b");
        var resp = endpoint.getHeldItems(req, null);
        assertNotNull(resp);
        assertNotNull(resp.getHeldItems());
    }

    @Test
    void getAbilityNamesHandlesNullList() {
        when(service.getAbilities("a")).thenReturn(null);
        var req = new com.pokeapi.application.soap.generated.GetAbilityNamesRequest();
        req.setName("a");
        var resp = endpoint.getAbilityNames(req, null);
        assertNotNull(resp);
    }

    @Test
    void getLocationAreaEncountersHandlesEmptyString() {
        when(service.getLocationAreaEncounters("")).thenReturn("");
        var req = new com.pokeapi.application.soap.generated.GetLocationAreaEncountersRequest();
        req.setName("");
        var resp = endpoint.getLocationAreaEncounters(req, null);
        assertNotNull(resp);
        assertEquals("", resp.getLocationAreaEncounters());
    }

    // --- new small mock tests (increase coverage) ---
    @Test
    void getNameHandlesServiceNull() {
        when(service.getName("n")).thenReturn(null);
        var req = new com.pokeapi.application.soap.generated.GetNameRequest(); req.setName("n");
        var resp = endpoint.getName(req, null);
        assertNotNull(resp);
        assertNull(resp.getName());
    }

    @Test
    void getBaseExperienceZero() {
        when(service.getBaseExperience("p")).thenReturn(0);
        var req = new com.pokeapi.application.soap.generated.GetBaseExperienceRequest(); req.setName("p");
        var resp = endpoint.getBaseExperience(req, null);
        assertNotNull(resp);
        assertEquals(0, resp.getBaseExperience());
    }

    @Test
    void getAbilityNamesHandlesSingle() {
        when(service.getAbilities("s")).thenReturn(java.util.List.of("only"));
        var req = new com.pokeapi.application.soap.generated.GetAbilityNamesRequest(); req.setName("s");
    var resp = endpoint.getAbilityNames(req, null);
    assertNotNull(resp);
    assertNotNull(resp.getAbilities());
    assertEquals(1, resp.getAbilities().getAbility().size());
    }

    @Test
    void getHeldItemsHandlesEmptyDomainList() {
        when(service.getHeldItems("x")).thenReturn(java.util.List.of());
        var req = new com.pokeapi.application.soap.generated.GetHeldItemsRequest(); req.setName("x");
    var resp = endpoint.getHeldItems(req, null);
    assertNotNull(resp);
    assertNotNull(resp.getHeldItems());
    assertEquals(0, resp.getHeldItems().getHeldItem().size());
    }

    @Test
    void getHeldItemsHandlesNullRequest() {
        var resp = endpoint.getHeldItems(null, null);
        assertNotNull(resp);
    }
}
