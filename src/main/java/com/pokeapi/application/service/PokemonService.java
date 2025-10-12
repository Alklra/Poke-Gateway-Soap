package com.pokeapi.application.service;

import org.springframework.stereotype.Service;
import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.port.PokemonRepository;

import java.util.List;

@Service
public class PokemonService {
    
    private final PokemonRepository pokemonRepository;
    
    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }
    
    public Long getId(String name) {
        return pokemonRepository.findByName(name).getId();
    }
    
    public String getName(String name) {
        return pokemonRepository.findByName(name).getName();
    }
    
    public List<String> getAbilities(String name) {
        return pokemonRepository.findByName(name).getAbilities();
    }
    
    public Integer getBaseExperience(String name) {
        return pokemonRepository.findByName(name).getBaseExperience();
    }
    
    public List<HeldItem> getHeldItems(String name) {
        return pokemonRepository.findByName(name).getHeldItems();
    }
    
    public String getLocationAreaEncounters(String name) {
        return pokemonRepository.findByName(name).getLocationAreaEncounters();
    }
}