package com.pokeapi.domain.port;

import com.pokeapi.domain.model.Pokemon;

public interface PokemonRepository {
    Pokemon findByName(String name);
}