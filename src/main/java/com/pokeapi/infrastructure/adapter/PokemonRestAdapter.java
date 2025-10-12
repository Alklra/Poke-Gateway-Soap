package com.pokeapi.infrastructure.adapter;

import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.port.PokemonRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class PokemonRestAdapter implements PokemonRepository {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    
    public PokemonRestAdapter(RestTemplate restTemplate, 
                            @Value("${pokeapi.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }
    
    @Override
    public Pokemon findByName(String name) {
        String url = baseUrl + "/pokemon/" + name.toLowerCase();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        return mapToPokemon(response);
    }
    
    private Pokemon mapToPokemon(JsonNode node) {
        if (node == null) {
            return null;
        }
        
        Pokemon pokemon = new Pokemon();
        pokemon.setId(node.path("id").asInt());
        pokemon.setName(node.path("name").asText());
        pokemon.setBaseExperience(node.path("base_experience").asInt());
        pokemon.setHeight(node.path("height").asInt());
        
        // Extraer nombres de habilidades
        List<String> abilities = new ArrayList<>();
        node.path("abilities").forEach(ability -> 
            abilities.add(ability.path("ability").path("name").asText()));
        pokemon.setAbilityNames(abilities);
        
        // Extraer stats base
        List<Integer> stats = new ArrayList<>();
        node.path("stats").forEach(stat -> 
            stats.add(stat.path("base_stat").asInt()));
        pokemon.setStatBaseValues(stats);
        
        return pokemon;
    }
}