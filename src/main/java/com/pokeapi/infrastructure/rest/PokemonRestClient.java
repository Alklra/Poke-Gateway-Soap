package com.pokeapi.infrastructure.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.pokeapi.domain.model.LocationAreaDto;


import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.model.HeldItem;
import com.pokeapi.domain.model.Item;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import com.pokeapi.domain.exception.ExternalServiceException;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PokemonRestClient {
    private static final Logger log = LoggerFactory.getLogger(PokemonRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    
    public PokemonRestClient(RestTemplate restTemplate, @Value("${pokeapi.base-url:https://pokeapi.co/api/v2}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Pokemon getPokemonByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        var lower = name.toLowerCase();
        String url = baseUrl + "/pokemon/" + lower;
        log.info("Se solicita a PokeAPI el pokemon: {} -> URL: {}", name, url);
        JsonNode response;
        try {
            response = restTemplate.getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException.NotFound nf) {
            log.info("PokeAPI 404 para pokemon {}: {}", name, nf.getMessage());
            throw new PokemonNotFoundException(name);
        } catch (HttpClientErrorException hce) {
            log.warn("Http error llamando a PokeAPI para {}: {}", name, hce.getMessage());
            throw new ExternalServiceException("PokeAPI", hce.getMessage());
        } catch (ResourceAccessException rae) {
            log.warn("Error de acceso a PokeAPI para {}: {}", name, rae.getMessage());
            throw new ExternalServiceException("PokeAPI", rae.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado llamando a PokeAPI para {}: {}", name, e.getMessage(), e);
            throw new ExternalServiceException("PokeAPI", e.getMessage());
        }

        if (response == null) {
            log.warn("PokeAPI devolvió null para pokemon: {}", name);
            throw new ExternalServiceException("PokeAPI", "Empty response");
        }
        // Loguear un fragmento seguro del JSON (evitar volcar objetos gigantes)
        try {
            var snippet = response.toString();
            if (snippet.length() > 1000) {
                snippet = snippet.substring(0, 1000) + "...[truncated]";
            }
            log.info("Se obtuvo respuesta JSON de PokeAPI para {}: {}", name, snippet);
        } catch (Exception e) {
            log.debug("No se pudo serializar JsonNode para logging: {}", e.getMessage());
        }

        return mapToPokemon(response);
    }
    
    private Pokemon mapToPokemon(JsonNode node) {
        if (node == null) {
            return null;
        }
        // Treat an empty JSON object as malformed response
        if (node.isObject() && node.size() == 0) {
            throw new ExternalServiceException("PokeAPI", "Empty JSON object");
        }
        // Extraer habilidades usando streams
        // abilities may contain wrappers or missing names; map safely to List<String>
        var abilityNodes = node.path("abilities");
        List<String> abilities = new ArrayList<>();
        if (abilityNodes != null && abilityNodes.isArray()) {
            for (var an : abilityNodes) {
                var abilityNode = an.path("ability");
                if (abilityNode != null && abilityNode.hasNonNull("name")) {
                    abilities.add(abilityNode.path("name").asText());
                }
            }
        }

        // Extraer held items con estilo funcional
        List<HeldItem> heldItems = new ArrayList<>();
        var heldNodes = node.path("held_items");
        if (heldNodes != null && heldNodes.isArray()) {
            for (var hn : heldNodes) {
                var itemNode = hn.path("item");
                if (itemNode != null && !itemNode.isNull()) {
                    var domainItem = Item.builder()
                        .name(itemNode.path("name").asText(null))
                        .url(itemNode.path("url").asText(null))
                        .build();
                    heldItems.add(HeldItem.builder().item(domainItem).build());
                } else {
                    heldItems.add(HeldItem.builder().item(null).build());
                }
            }
        }

        var location = Optional.ofNullable(node.path("location_area_encounters").asText(null)).orElse("");

        return Pokemon.builder()
            .id(node.path("id").asLong())
            .name(node.path("name").asText())
            .abilities(abilities)
            .baseExperience(node.path("base_experience").asInt())
            .heldItems(heldItems)
            .locationAreaEncounters(location)
            .build();
    }

    /**
     * Realiza la llamada a la URL de encounters (proporcionada por el endpoint /pokemon/{name})
     * y mapea la respuesta (array JSON) a la clase generated LocationAreas.
     * Si la URL es vacía o nula, devuelve null.
     */
    public List<LocationAreaDto> fetchLocationAreas(String encountersUrl) {
        if (encountersUrl == null || encountersUrl.isBlank()) return List.of();
        JsonNode resp;
        try {
            log.info("Fetching location areas from {}", encountersUrl);
            resp = restTemplate.getForObject(encountersUrl, JsonNode.class);
        } catch (HttpClientErrorException.NotFound nf) {
            log.info("Encounters endpoint not found: {}", encountersUrl);
            return List.of();
        } catch (Exception e) {
            log.warn("Error calling encounters URL {}: {}", encountersUrl, e.getMessage());
            throw new ExternalServiceException("PokeAPI", e.getMessage());
        }

        if (resp == null || !resp.isArray()) {
            return List.of();
        }

        var out = new ArrayList<LocationAreaDto>();
        for (var el : resp) {
            var la = el.path("location_area");
            if (la == null || la.isMissingNode()) continue;
            var name = la.path("name").asText(null);
            var url = la.path("url").asText(null);
            if (name == null && url == null) continue;
            out.add(new LocationAreaDto(name == null ? "" : name, url == null ? "" : url));
        }
        log.info("Parsed {} location areas from {}", out.size(), encountersUrl);
        return out;
    }
}