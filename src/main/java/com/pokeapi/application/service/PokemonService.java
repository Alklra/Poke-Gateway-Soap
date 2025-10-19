package com.pokeapi.application.service;

import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.model.LocationAreaDto;
import com.pokeapi.domain.port.PokemonRepository;
import com.pokeapi.infrastructure.rest.PokemonRestClient;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Collections;

@Service
public class PokemonService {
    private final PokemonRepository pokemonRepository;
    private final PokemonRestClient pokemonRestClient;

    @Autowired
    public PokemonService(PokemonRepository pokemonRepository, PokemonRestClient pokemonRestClient) {
        this.pokemonRepository = pokemonRepository;
        this.pokemonRestClient = pokemonRestClient;
    }

    // Backwards compatibility for tests that construct service with only repository
    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
        this.pokemonRestClient = null;
    }

    public Long getId(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? null : p.getId();
    }
    public String getName(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? null : p.getName();
    }
    public Integer getBaseExperience(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? null : p.getBaseExperience();
    }

    public List<String> getAbilities(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? Collections.emptyList() : p.getAbilities();
    }

    public List<HeldItem> getHeldItems(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? Collections.emptyList() : p.getHeldItems();
    }
    public String getLocationAreaEncounters(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? null : p.getLocationAreaEncounters();
    }

    public List<LocationAreaDto> getLocationAreas(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        var url = p == null ? null : p.getLocationAreaEncounters();
        if (pokemonRestClient == null) return Collections.emptyList();
        return pokemonRestClient.fetchLocationAreas(url);
    }
}