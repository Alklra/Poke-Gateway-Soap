package com.pokeapi.infrastructure.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.pokeapi.domain.model.Pokemon;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PokemonRestClient {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    
    public PokemonRestClient(RestTemplate restTemplate, @Value("${pokeapi.base-url:https://pokeapi.co/api/v2}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }
    
    public Pokemon getPokemonByName(String name) {
        String url = baseUrl + "/pokemon/" + name.toLowerCase();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        
        return mapToPokemon(response);
    }
    
    private Pokemon mapToPokemon(JsonNode node) {
        if (node == null) {
            return null;
        }
        
        Pokemon pokemon = new Pokemon();
        pokemon.setId(node.path("id").asLong());
        pokemon.setName(node.path("name").asText());
        pokemon.setBaseExperience(node.path("base_experience").asInt());
        pokemon.setHeight(node.path("height").asInt());
        pokemon.setWeight(node.path("weight").asInt());
        
        // Extraer habilidades
        StringBuilder abilities = new StringBuilder();
        node.path("abilities").forEach(ability -> {
            if (abilities.length() > 0) {
                abilities.append(", ");
            }
            abilities.append(ability.path("ability").path("name").asText());
        });
        pokemon.setAbilities(abilities.toString());
        
        return pokemon;
    }
}