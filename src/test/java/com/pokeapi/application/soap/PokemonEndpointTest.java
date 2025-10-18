package com.pokeapi.application.soap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pokeapi.application.soap.generated.*;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import com.pokeapi.application.service.PokemonService;
import com.pokeapi.domain.exception.ExternalServiceException;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.model.Item;
import java.lang.reflect.Method;
import org.springframework.ws.WebServiceMessage;
import com.pokeapi.domain.model.LocationAreaDto;

import java.util.List;
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
        when(service.getId("err")).thenThrow(new ExternalServiceException("pokeapi","down"));

        var resp = endpoint.getId(req, messageContext);
        assertNull(resp);
        verify(service).getId("err");
    }
    */

    // from PokemonEndpointHeldItemsTest
    @Test
    void getHeldItemsMapsCorrectly_combined() {
        Item item = Item.builder().name("i").url("u").build();
        HeldItem hi = HeldItem.builder().item(item).build();
        when(service.getHeldItems("bulbasaur")).thenReturn(List.of(hi));

        var req = new GetHeldItemsRequest();
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

        when(service.getId("missing")).thenThrow(new PokemonNotFoundException("missing"));
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
        var req = new GetNameRequest();
        req.setName("x");
        var resp = endpoint.getName(req, null);
        assertNotNull(resp);
    }

    @Test
    void getNameNotFound() {
        when(service.getName("m")).thenThrow(new PokemonNotFoundException("m"));
        var req = new GetNameRequest();
        req.setName("m");
        var resp = endpoint.getName(req, null);
        assertNull(resp);
    }

    @Test
    void getBaseExperienceHappy() {
        when(service.getBaseExperience("b")).thenReturn(12);
        var req = new GetBaseExperienceRequest();
        req.setName("b");
        var resp = endpoint.getBaseExperience(req, null);
        assertNotNull(resp);
    }

    @Test
    void getBaseExperienceNotFound() {
        when(service.getBaseExperience("n")).thenThrow(new PokemonNotFoundException("n"));
        var req = new GetBaseExperienceRequest();
        req.setName("n");
        var resp = endpoint.getBaseExperience(req, null);
        assertNull(resp);
    }

    @Test
    void getAbilityNamesHappy() {
        when(service.getAbilities("a")).thenReturn(List.of("one","two"));
        var req = new GetAbilityNamesRequest();
        req.setName("a");
    var resp = endpoint.getAbilityNames(req, null);
    assertNotNull(resp);
    assertNotNull(resp.getAbilities());
    assertEquals(2, resp.getAbilities().getAbility().size());
    }

    @Test
    void getAbilityNamesEmpty() {
        when(service.getAbilities("e")).thenReturn(List.of());
        var req = new GetAbilityNamesRequest();
        req.setName("e");
    var resp = endpoint.getAbilityNames(req, null);
    assertNotNull(resp);
    }

    @Test
    void getLocationAreaEncountersHappy() {
        when(service.getLocationAreas("l")).thenReturn(List.of(new LocationAreaDto("locname","http://loc")));
        var req = new GetLocationAreaEncountersRequest();
        req.setName("l");
        var resp = endpoint.getLocationAreaEncounters(req, null);
        assertNotNull(resp);
        assertNotNull(resp.getLocationAreaEncounters());
        assertEquals(1, resp.getLocationAreaEncounters().getLocationArea().size());
    }

    @Test
    void getLocationAreaEncountersNotFound() {
        when(service.getLocationAreas("nf")).thenThrow(new PokemonNotFoundException("nf"));
        var req = new GetLocationAreaEncountersRequest();
        req.setName("nf");
        var resp = endpoint.getLocationAreaEncounters(req, null);
        assertNull(resp);
    }

    @Test
    void getHeldItemsEmptyWhenServiceReturnsNull() {
        when(service.getHeldItems("z")).thenReturn(null);
        var req = new GetHeldItemsRequest();
        req.setName("z");
        var resp = endpoint.getHeldItems(req, null);
        assertNotNull(resp);
    }

    @Test
    void endpointHandlesNullNameGracefully() {
        var req = new GetIdRequest();
        req.setName(null);
        var resp = endpoint.getId(req, null);
        assertNull(resp);
    }

    @Test
    void endpointHandlesEmptyNameGracefully() {
        var req = new GetIdRequest();
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
        when(service.getId("err")).thenThrow(new ExternalServiceException("poke","down"));
        var req = new GetIdRequest(); req.setName("err");
        var resp = endpoint.getId(req, dmc);
        assertNull(resp);
        assertTrue(ssm.getSaajMessage().getSOAPBody().hasFault());
    }

    @Test
    void buildSoapFaultHandlesNonSaajResponse() {
        // return a response object that is not SaajSoapMessage
        MessageContext mc = mock(MessageContext.class);
        WebServiceMessage fakeResponse = mock(WebServiceMessage.class);
        when(mc.getResponse()).thenReturn(fakeResponse);
        when(service.getId("err2")).thenThrow(new ExternalServiceException("poke","down"));
        var req = new GetIdRequest(); req.setName("err2");
        var resp = endpoint.getId(req, mc);
        assertNull(resp);
        // nothing to assert on fake response, but code path executed
    }

    // New tests to increase coverage (edge cases and null handling)
    @Test
    void getHeldItemsHandlesNullItemInDomain() {
        HeldItem hi = HeldItem.builder().item(null).build();
        when(service.getHeldItems("b")).thenReturn(List.of(hi));
        var req = new GetHeldItemsRequest();
        req.setName("b");
        var resp = endpoint.getHeldItems(req, null);
        assertNotNull(resp);
        assertNotNull(resp.getHeldItems());
    }

    // Tests moved from the private helper test file per request
    @Test
    void setPropertyIfPossibleHandlesMissingFieldAndPrimitive() throws Exception {
        var endpoint = new PokemonEndpoint(null);
        Method m = PokemonEndpoint.class.getDeclaredMethod("setPropertyIfPossible", Object.class, String.class, Object.class);
        m.setAccessible(true);

        Object target = new Object();
        assertDoesNotThrow(() -> m.invoke(endpoint, target, "nonexistent", "value"));

        class Holder { public int x = 5; }
        Holder h = new Holder();
        assertDoesNotThrow(() -> m.invoke(endpoint, h, "x", null));
        assertEquals(5, h.x);
    }

    @Test
    void getAbilityNamesHandlesNullList() {
        when(service.getAbilities("a")).thenReturn(null);
        var req = new GetAbilityNamesRequest();
        req.setName("a");
        var resp = endpoint.getAbilityNames(req, null);
        assertNotNull(resp);
    }

    @Test
    void getLocationAreaEncountersHandlesEmptyString() {
        when(service.getLocationAreas("")).thenReturn(List.of());
        var req = new GetLocationAreaEncountersRequest();
        req.setName("");
        var resp = endpoint.getLocationAreaEncounters(req, null);
        assertNotNull(resp);
        assertNotNull(resp.getLocationAreaEncounters());
        assertEquals(0, resp.getLocationAreaEncounters().getLocationArea().size());
    }

    // --- new small mock tests (increase coverage) ---
    @Test
    void getNameHandlesServiceNull() {
        when(service.getName("n")).thenReturn(null);
        var req = new GetNameRequest(); req.setName("n");
        var resp = endpoint.getName(req, null);
        assertNotNull(resp);
        assertNull(resp.getName());
    }

    @Test
    void getBaseExperienceZero() {
        when(service.getBaseExperience("p")).thenReturn(0);
        var req = new GetBaseExperienceRequest(); req.setName("p");
        var resp = endpoint.getBaseExperience(req, null);
        assertNotNull(resp);
        assertEquals(0, resp.getBaseExperience());
    }

    @Test
    void getAbilityNamesHandlesSingle() {
        when(service.getAbilities("s")).thenReturn(List.of("only"));
        var req = new GetAbilityNamesRequest(); req.setName("s");
    var resp = endpoint.getAbilityNames(req, null);
    assertNotNull(resp);
    assertNotNull(resp.getAbilities());
    assertEquals(1, resp.getAbilities().getAbility().size());
    }

    @Test
    void getHeldItemsHandlesEmptyDomainList() {
        when(service.getHeldItems("x")).thenReturn(List.of());
        var req = new GetHeldItemsRequest(); req.setName("x");
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
