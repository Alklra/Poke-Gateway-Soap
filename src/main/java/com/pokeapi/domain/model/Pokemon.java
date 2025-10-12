package com.pokeapi.domain.model;

import java.util.List;

public class Pokemon {
    private Integer id;
    private String name;
    private Integer baseExperience;
    private Integer height;
    private List<String> abilityNames;
    private List<Integer> statBaseValues;

    // Constructor
    public Pokemon(Integer id, String name, Integer baseExperience, Integer height, 
                  List<String> abilityNames, List<Integer> statBaseValues) {
        this.id = id;
        this.name = name;
        this.baseExperience = baseExperience;
        this.height = height;
        this.abilityNames = abilityNames;
        this.statBaseValues = statBaseValues;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBaseExperience() {
        return baseExperience;
    }

    public Integer getHeight() {
        return height;
    }

    public List<String> getAbilityNames() {
        return abilityNames;
    }

    public List<Integer> getStatBaseValues() {
        return statBaseValues;
    }
}