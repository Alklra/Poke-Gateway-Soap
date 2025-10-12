package com.pokeapi.domain.service;

import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.port.PokemonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonService {
    private final PokemonRepository pokemonRepository;

    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public Integer getId(String name) {
        return pokemonRepository.findByName(name).getId();
    }

    public String getName(String name) {
        return pokemonRepository.findByName(name).getName();
    }

    public Integer getBaseExperience(String name) {
        return pokemonRepository.findByName(name).getBaseExperience();
    }

    public Integer getHeight(String name) {
        return pokemonRepository.findByName(name).getHeight();
    }

    public List<String> getAbilityNames(String name) {
        return pokemonRepository.findByName(name).getAbilityNames();
    }

    public List<Integer> getStatBaseValues(String name) {
        return pokemonRepository.findByName(name).getStatBaseValues();
    }
}