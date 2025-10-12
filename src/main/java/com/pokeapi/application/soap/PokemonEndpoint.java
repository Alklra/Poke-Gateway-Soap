package com.pokeapi.application.soap;

import com.pokeapi.domain.service.PokemonService;
import com.pokeapi.application.soap.generated.*;
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
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBaseExperienceRequest")
    @ResponsePayload
    public GetBaseExperienceResponse getBaseExperience(@RequestPayload GetBaseExperienceRequest request) {
        GetBaseExperienceResponse response = new GetBaseExperienceResponse();
        response.setBaseExperience(pokemonService.getBaseExperience(request.getName()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHeightRequest")
    @ResponsePayload
    public GetHeightResponse getHeight(@RequestPayload GetHeightRequest request) {
        GetHeightResponse response = new GetHeightResponse();
        response.setHeight(pokemonService.getHeight(request.getName()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAbilityNamesRequest")
    @ResponsePayload
    public GetAbilityNamesResponse getAbilityNames(@RequestPayload GetAbilityNamesRequest request) {
        GetAbilityNamesResponse response = new GetAbilityNamesResponse();
        response.setAbilities(new Abilities());
        response.getAbilities().getAbility().addAll(pokemonService.getAbilityNames(request.getName()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStatBaseValuesRequest")
    @ResponsePayload
    public GetStatBaseValuesResponse getStatBaseValues(@RequestPayload GetStatBaseValuesRequest request) {
        GetStatBaseValuesResponse response = new GetStatBaseValuesResponse();
        response.setStats(new Stats());
        response.getStats().getBaseStat().addAll(pokemonService.getStatBaseValues(request.getName()));
        return response;
    }
}