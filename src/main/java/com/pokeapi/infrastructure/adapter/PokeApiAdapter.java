package com.pokeapi.infrastructure.adapter;

import com.pokeapi.infrastructure.adapter.rest.dto.PokemonResponse;
import com.pokeapi.domain.exception.ExternalServiceException;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.port.PokemonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import com.pokeapi.infrastructure.adapter.rest.dto.HeldItemWrapper;
import com.pokeapi.infrastructure.adapter.rest.dto.HeldItemWrapper.ItemWrapper;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.model.Item;

@Component
public class PokeApiAdapter implements PokemonRepository {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PokeApiAdapter(RestTemplate restTemplate, @Value("${pokeapi.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private List<HeldItem> mapHeldItems(List<HeldItemWrapper> wrappers) {
        List<HeldItem> result = new ArrayList<>();
        if (wrappers == null) return result;
        for (HeldItemWrapper w : wrappers) {
            HeldItem hi = new HeldItem();
            if (w != null && w.getItem() != null) {
                Item domainItem = Item.builder()
                    .name(w.getItem().getName())
                    .url(w.getItem().getUrl())
                    .build();
                hi.setItem(domainItem);
            }
            result.add(hi);
        }
        return result;
    }

    @Override
    public Pokemon findByName(String name) {
        try {
            PokemonResponse response = restTemplate.getForObject(
                baseUrl + "/pokemon/{name}",
                PokemonResponse.class,
                name.toLowerCase()
            );

            if (response == null) {
                throw new PokemonNotFoundException(name);
            }

            return Pokemon.builder()
                .id(response.getId().longValue())
                .name(response.getName())
                .baseExperience(response.getBaseExperience())
                .abilities(response.getAbilities().stream()
                    .map(ability -> ability.getAbility().getName())
                    .collect(Collectors.toList()))
                .heldItems(mapHeldItems(response.getHeldItems()))
                .locationAreaEncounters(response.getLocationAreaEncounters())
                .build();
        } catch (HttpClientErrorException.NotFound e) {
            throw new PokemonNotFoundException(name);
        } catch (Exception e) {
            throw new ExternalServiceException("PokeAPI", e.getMessage());
        }
    }
}