package com.pokeapi.domain.exception;

public class PokemonNotFoundException extends BusinessException {
    private static final String CODE = "POKEMON_NOT_FOUND";
    
    public PokemonNotFoundException(String pokemonName) {
        super(CODE, String.format("Pokemon with name '%s' not found", pokemonName));
    }
}