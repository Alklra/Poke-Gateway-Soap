package com.pokeapi.application.service;

import org.springframework.stereotype.Service;
import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.port.PokemonRepository;

import java.util.List;
import java.util.Collections;

@Service
public class PokemonService {
    
    private final PokemonRepository pokemonRepository;
    
    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
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
    
    public List<String> getAbilities(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? Collections.emptyList() : p.getAbilities();
    }
    
    public Integer getBaseExperience(String name) {
        if (name == null) throw new IllegalArgumentException("name");
        var p = pokemonRepository.findByName(name);
        return p == null ? null : p.getBaseExperience();
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
}