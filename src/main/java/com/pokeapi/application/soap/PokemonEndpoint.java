package com.pokeapi.application.soap;

import com.pokeapi.application.service.PokemonService;
import com.pokeapi.domain.model.HeldItem;

import com.pokeapi.application.soap.generated.*;
import com.pokeapi.application.soap.generated.LocationAreas.LocationArea;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import jakarta.xml.soap.SOAPFault;
import javax.xml.namespace.QName;

import com.pokeapi.domain.exception.BusinessException;
import com.pokeapi.domain.exception.ExternalServiceException;
import com.pokeapi.domain.exception.PokemonNotFoundException;


@Endpoint
public class PokemonEndpoint {
    private static final Logger log = LoggerFactory.getLogger(PokemonEndpoint.class);
    // Namespace definido en el XSD
    private static final String NAMESPACE_URI = "http://www.pokegateway.com/soap/gen";
    // Fault constants
    private static final String FAULT_TYPE_SERVER = "Server";
    private static final String FAULT_TYPE_CLIENT = "Client";
    private static final String CODE_INTERNAL_ERROR = "INTERNAL_ERROR";
    private static final String MSG_UNEXPECTED_SERVER = "Unexpected server error";
    private static final String CODE_EXTERNAL_SERVICE = "EXTERNAL_SERVICE_ERROR";
    private final PokemonService pokemonService;

    public PokemonEndpoint(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getIdRequest")
    @ResponsePayload
    public GetIdResponse getId(@RequestPayload GetIdRequest request, MessageContext messageContext) {
        try {
            if (request == null || request.getName() == null || request.getName().isBlank()) return null;
            GetIdResponse response = new GetIdResponse();
            // set id via reflection to avoid possible generated-class mismatches at runtime
            Object idVal = pokemonService.getId(request.getName());
            setPropertyIfPossible(response, "id", idVal);
            return response;
        } catch (PokemonNotFoundException pnfe) {
            log.info("Pokemon not found: {}", request.getName());
            buildSoapFault(messageContext, "Client", pnfe.getCode(), pnfe.getMessage());
            return null;
        } catch (ExternalServiceException ese) {
            log.warn("External service error while getting id for {}: {}", request.getName(), ese.getMessage());
            buildSoapFault(messageContext, FAULT_TYPE_SERVER, CODE_EXTERNAL_SERVICE, ese.getMessage());
            return null;
        } catch (BusinessException be) {
            log.warn("Business exception: {}", be.getMessage());
            buildSoapFault(messageContext, FAULT_TYPE_CLIENT, be.getCode(), be.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error in getId: {}", e.getMessage(), e);
            buildSoapFault(messageContext, FAULT_TYPE_SERVER, CODE_INTERNAL_ERROR, MSG_UNEXPECTED_SERVER);
            return null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNameRequest")
    @ResponsePayload
    public GetNameResponse getName(@RequestPayload GetNameRequest request, MessageContext messageContext) {
        try {
            if (request == null || request.getName() == null || request.getName().isBlank()) return null;
            GetNameResponse response = new GetNameResponse();
            response.setName(pokemonService.getName(request.getName()));
            return response;
        } catch (Exception e) {
            log.error("Error en getName: {}", e.getMessage(), e);
            buildSoapFault(messageContext, FAULT_TYPE_SERVER, CODE_INTERNAL_ERROR, MSG_UNEXPECTED_SERVER);
            return null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBaseExperienceRequest")
    @ResponsePayload
    public GetBaseExperienceResponse getBaseExperience(@RequestPayload GetBaseExperienceRequest request, MessageContext messageContext) {
        try {
            if (request == null || request.getName() == null || request.getName().isBlank()) return null;
            GetBaseExperienceResponse response = new GetBaseExperienceResponse();
            Object be = pokemonService.getBaseExperience(request.getName());
            log.info("Base experience for {}: {}", request.getName(), be);
            setPropertyIfPossible(response, "baseExperience", be);
            return response;
        } catch (Exception e) {
            log.error("Error en getBaseExperience: {}", e.getMessage(), e);
            buildSoapFault(messageContext, FAULT_TYPE_SERVER, CODE_INTERNAL_ERROR, MSG_UNEXPECTED_SERVER);
            return null;
        }
    }

    /** CORRECCIÓN: Se renombran las clases y localPart de 'getAbilities' a 'getAbilityNames'
     * para coincidir con el XSD limpio. */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAbilityNamesRequest")
    @ResponsePayload
    public GetAbilityNamesResponse getAbilityNames(@RequestPayload GetAbilityNamesRequest request, MessageContext messageContext) { // <-- Clases renombradas
        try {
            if (request == null || request.getName() == null || request.getName().isBlank()) return null;
            GetAbilityNamesResponse response = new GetAbilityNamesResponse();
            Abilities abilities = new Abilities();
            var domain = pokemonService.getAbilities(request.getName());
            if (domain != null) abilities.getAbility().addAll(domain);
            setPropertyIfPossible(response, "abilities", abilities);
            return response;
        } catch (Exception e) {
            log.error("Error en getAbilityNames: {}", e.getMessage(), e);
            buildSoapFault(messageContext, FAULT_TYPE_SERVER, CODE_INTERNAL_ERROR, MSG_UNEXPECTED_SERVER);
            return null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHeldItemsRequest")
    @ResponsePayload
    public GetHeldItemsResponse getHeldItems(@RequestPayload GetHeldItemsRequest request, MessageContext messageContext) {
        try {
            GetHeldItemsResponse response = new GetHeldItemsResponse();
            // Convert domain HeldItem list to generated HeldItems using streams (Java 21+ style)
            var genHeldItems = new HeldItems();
            var domainHeld = request == null ? null : pokemonService.getHeldItems(request.getName());
            log.info("Held items for {}: {} entries - {}", request.getName(), (domainHeld == null ? 0 : domainHeld.size()), (domainHeld == null ? "[]" : domainHeld.toString()));
            if (domainHeld != null) {
                for (var dh : domainHeld) {
                    com.pokeapi.application.soap.generated.HeldItem gh = new com.pokeapi.application.soap.generated.HeldItem();
                    com.pokeapi.application.soap.generated.Item gi = new com.pokeapi.application.soap.generated.Item();
                    var di = dh.getItem();
                    if (di != null) {
                        gi.setName(di.getName());
                        gi.setUrl(di.getUrl());
                    }
                    gh.setItem(gi);
                    genHeldItems.getHeldItem().add(gh);
                }
            }
            setPropertyIfPossible(response, "heldItems", genHeldItems);
            return response;
        } catch (Exception e) {
            log.error("Error en getHeldItems: {}", e.getMessage(), e);
            if (messageContext != null) buildSoapFault(messageContext, FAULT_TYPE_SERVER, CODE_INTERNAL_ERROR, MSG_UNEXPECTED_SERVER);
            return null;
        }
    }

    private void setPropertyIfPossible(Object target, String fieldName, Object value) {
        if (target == null) return;
        try {
            var cls = target.getClass();
            var f = cls.getDeclaredField(fieldName);
            f.setAccessible(true);
            if (value == null && f.getType().isPrimitive()) {
                return; // keep default
            }
            f.set(target, value);
            return;
        } catch (NoSuchFieldException nsf) {
            // field not present, nothing to do
            return;
        } catch (Exception e) {
            log.debug("Could not set property {} on {}: {}", fieldName, target.getClass().getSimpleName(), e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getLocationAreaEncountersRequest")
    @ResponsePayload
    public GetLocationAreaEncountersResponse getLocationAreaEncounters(@RequestPayload GetLocationAreaEncountersRequest request, MessageContext messageContext) {
        try {
            GetLocationAreaEncountersResponse response = new GetLocationAreaEncountersResponse();
            var list = pokemonService.getLocationAreas(request.getName());
            log.info("Location areas for {}: {} entries - {}", request.getName(), (list == null ? 0 : list.size()), (list == null ? "[]" : list.toString()));
            // construir LocationAreas directamente usando las clases generadas
            LocationAreas locAreas = new LocationAreas();
            if (list != null) {
                for (var dto : list) {
                    LocationArea la = new LocationArea();
                    la.setName(dto.getName());
                    la.setUrl(dto.getUrl());
                    locAreas.getLocationArea().add(la);
                }
            }
            response.setLocationAreaEncounters(locAreas);
            return response;
        } catch (Exception e) {
            log.error("Error en getLocationAreaEncounters: {}", e.getMessage(), e);
            buildSoapFault(messageContext, FAULT_TYPE_SERVER, CODE_INTERNAL_ERROR, MSG_UNEXPECTED_SERVER);
            return null;
        }
    }

    // Helper para construir un SOAP Fault en la respuesta.
    private void buildSoapFault(MessageContext messageContext, String faultType, String code, String message) {
        try {
            if (messageContext == null) {
                log.warn("No messageContext provided, cannot build SoapFault");
                return;
            }
            var response = messageContext.getResponse();
            if (response instanceof SaajSoapMessage) {
                SaajSoapMessage saaj = (SaajSoapMessage) response;
                if (saaj.getSaajMessage() == null || saaj.getSaajMessage().getSOAPBody() == null) {
                    log.warn("Saaj message or SOAP body is null, cannot add fault");
                    return;
                }
                SOAPFault fault = saaj.getSaajMessage().getSOAPBody().addFault();
                // Usar namespace SOAP 1.1 por compatibilidad
                QName faultCode = new QName("http://schemas.xmlsoap.org/soap/envelope/", faultType);
                fault.setFaultCode(faultCode);
                // Incluir código de negocio y mensaje
                String text = String.format("%s: %s - %s", code, message, faultType);
                fault.setFaultString(text);
            } else {
                log.warn("Respuesta no es SaajSoapMessage, no se puede construir SOAP Fault");
            }
        } catch (Exception e) {
            log.error("Error construyendo SoapFault: {}", e.getMessage(), e);
        }
    }
}