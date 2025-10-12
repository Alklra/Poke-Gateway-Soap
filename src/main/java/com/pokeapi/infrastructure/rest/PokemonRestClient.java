package com.pokeapi.infrastructure.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.model.Item;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

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
        
        // Extraer habilidades
        List<String> abilities = new ArrayList<>();
        node.path("abilities").forEach(ability -> {
            abilities.add(ability.path("ability").path("name").asText());
        });

        // Extraer held items
        List<HeldItem> heldItems = new ArrayList<>();
        node.path("held_items").forEach(heldItemNode -> {
            HeldItem heldItem = HeldItem.builder()
                .item(Item.builder()
                    .name(heldItemNode.path("item").path("name").asText())
                    .url(heldItemNode.path("item").path("url").asText())
                    .build())
                .build();
            heldItems.add(heldItem);
        });

        return Pokemon.builder()
            .id(node.path("id").asLong())
            .name(node.path("name").asText())
            .abilities(abilities)
            .baseExperience(node.path("base_experience").asInt())
            .heldItems(heldItems)
            .locationAreaEncounters(node.path("location_area_encounters").asText())
            .build();
    }
}