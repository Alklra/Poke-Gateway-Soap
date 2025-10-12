package com.pokeapi.infrastructure.adapter;

import com.pokeapi.infrastructure.adapter.rest.dto.PokemonResponse;
import com.pokeapi.domain.exception.ExternalServiceException;
import com.pokeapi.domain.exception.PokemonNotFoundException;
import com.pokeapi.domain.model.Pokemon;
import com.pokeapi.domain.port.PokemonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Component
public class PokeApiAdapter implements PokemonRepository {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PokeApiAdapter(RestTemplate restTemplate, @Value("${pokeapi.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Pokemon findByName(String name) {
        try {
            PokemonResponse response = restTemplate.getForObject(
                baseUrl + "/pokemon/{name}",
                PokemonResponse.class,
                name.toLowerCase()
            );

            if (response == null) {
                throw new PokemonNotFoundException(name);
            }

            return new Pokemon(
                response.getId(),
                response.getName(),
                response.getBaseExperience(),
                response.getHeight(),
                response.getAbilities().stream()
                    .map(ability -> ability.getAbility().getName())
                    .collect(Collectors.toList()),
                response.getStats().stream()
                    .map(PokemonResponse.StatWrapper::getBaseStat)
                    .collect(Collectors.toList())
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new PokemonNotFoundException(name);
        } catch (Exception e) {
            throw new ExternalServiceException("PokeAPI", e.getMessage());
        }
    }
}