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

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.pokeapi.infrastructure.adapter.rest.dto.HeldItemWrapper;
import com.pokeapi.infrastructure.adapter.rest.dto.HeldItemWrapper.ItemWrapper;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.model.Item;

@Component
public class PokeApiAdapter implements PokemonRepository {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private static final Logger log = LoggerFactory.getLogger(PokeApiAdapter.class);

    public PokeApiAdapter(RestTemplate restTemplate, @Value("${pokeapi.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private List<HeldItem> mapHeldItems(List<HeldItemWrapper> wrappers) {
        if (wrappers == null) return List.of();
        return wrappers.stream()
            .map(w -> {
                var iw = (w == null) ? null : w.getItem();
                var domainItem = (iw == null) ? null
                    : Item.builder()
                        .name(iw.getName())
                        .url(iw.getUrl())
                        .build();
                return HeldItem.builder().item(domainItem).build();
            })
            .toList();
    }

    @Override
    public Pokemon findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        try {
            String url = baseUrl + "/pokemon/" + name.trim().toLowerCase();
            var response = restTemplate.getForObject(url, PokemonResponse.class);
            // log the request and a small snippet of the response
            try {
                var snip = response == null ? "null" : (response.getName() + " id=" + response.getId());
                log.info("PokeApiAdapter GET {} -> {}", url, snip);
            } catch (Exception ex) {
                log.debug("Could not build response snippet for logging: {}", ex.getMessage());
            }

            // additional debug: show how many held_items and the encounters URL (if present)
            try {
                int heldCount = response == null || response.getHeldItems() == null ? 0 : response.getHeldItems().size();
                String encountersUrl = response == null ? null : response.getLocationAreaEncounters();
                log.info("PokeApiAdapter: held_items count = {} , location_area_encounters = {}", heldCount, encountersUrl);
            } catch (Exception ex) {
                log.debug("Could not build held items / encounters logging: {}", ex.getMessage());
            }

            var resp = Optional.ofNullable(response).orElseThrow(() -> new PokemonNotFoundException(name));

            var id = Optional.ofNullable(resp.getId()).map(Integer::longValue).orElse(null);
            List<String> abilities = new ArrayList<>();
            if (resp.getAbilities() != null) {
                for (var aw : resp.getAbilities()) {
                    if (aw != null && aw.getAbility() != null && aw.getAbility().getName() != null) {
                        abilities.add(aw.getAbility().getName());
                    }
                }
            }

            return Pokemon.builder()
                .id(id)
                .name(resp.getName())
                .baseExperience(resp.getBaseExperience()) // Allow null values
                .abilities(abilities)
                .heldItems(mapHeldItems(resp.getHeldItems()))
                .locationAreaEncounters(resp.getLocationAreaEncounters())
                .build();
        } catch (HttpClientErrorException.NotFound e) {
            throw new PokemonNotFoundException(name);
        } catch (Exception e) {
            throw new ExternalServiceException("PokeAPI", e.getMessage());
        }
    }
}