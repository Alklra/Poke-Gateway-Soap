package com.pokeapi.application.soap;

import com.pokeapi.domain.service.PokemonService;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.application.soap.generated.*;
import java.util.List;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class PokemonEndpoint {
    private static final String NAMESPACE_URI = "http://localhost:8080/pokeapi/soap";
    private final PokemonService pokemonService;

    public PokemonEndpoint(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getIdRequest")
    @ResponsePayload
    public GetIdResponse getId(@RequestPayload GetIdRequest request) {
        GetIdResponse response = new GetIdResponse();
        response.setId(pokemonService.getId(request.getName()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNameRequest")
    @ResponsePayload
    public GetNameResponse getName(@RequestPayload GetNameRequest request) {
        GetNameResponse response = new GetNameResponse();
        response.setName(pokemonService.getName(request.getName()));
        return response;
    }@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBaseExperienceRequest")
    @ResponsePayload
    public GetBaseExperienceResponse getBaseExperience(@RequestPayload GetBaseExperienceRequest request) {
        GetBaseExperienceResponse response = new GetBaseExperienceResponse();
        response.setBaseExperience(pokemonService.getBaseExperience(request.getName()));
        return response;
    }

    /** CORRECCIÓN: Se renombran las clases y localPart de 'getAbilities' a 'getAbilityNames'
     * para coincidir con el XSD limpio. */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAbilityNamesRequest")
    @ResponsePayload
    public GetAbilityNamesResponse getAbilityNames(@RequestPayload GetAbilityNamesRequest request) { // <-- Clases renombradas
        GetAbilityNamesResponse response = new GetAbilityNamesResponse();
        
        // La lógica de inicialización para la lista es correcta
        response.setAbilities(new Abilities()); 
        response.getAbilities().getAbility().addAll(pokemonService.getAbilities(request.getName())); 
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHeldItemsRequest")
    @ResponsePayload
    public GetHeldItemsResponse getHeldItems(@RequestPayload GetHeldItemsRequest request) {
        GetHeldItemsResponse response = new GetHeldItemsResponse();
        // Convert domain HeldItem list to generated HeldItems
        HeldItems genHeldItems = new HeldItems();
        List<com.pokeapi.domain.model.HeldItem> domainHeld = pokemonService.getHeldItems(request.getName());
        if (domainHeld != null) {
            for (com.pokeapi.domain.model.HeldItem dh : domainHeld) {
                com.pokeapi.application.soap.generated.HeldItem gh = new com.pokeapi.application.soap.generated.HeldItem();
                com.pokeapi.application.soap.generated.Item gi = new com.pokeapi.application.soap.generated.Item();
                if (dh.getItem() != null) {
                    gi.setName(dh.getItem().getName());
                    gi.setUrl(dh.getItem().getUrl());
                }
                gh.setItem(gi);
                genHeldItems.getHeldItem().add(gh);
            }
        }
        response.setHeldItems(genHeldItems);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getLocationAreaEncountersRequest")
    @ResponsePayload
    public GetLocationAreaEncountersResponse getLocationAreaEncounters(@RequestPayload GetLocationAreaEncountersRequest request) {
        GetLocationAreaEncountersResponse response = new GetLocationAreaEncountersResponse();
        response.setLocationAreaEncounters(pokemonService.getLocationAreaEncounters(request.getName()));
        return response;
    }
}