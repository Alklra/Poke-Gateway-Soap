package com.pokeapi.infrastructure.adapter.rest.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonResponse {
    private Integer id;
    private String name;
    @JsonProperty("base_experience")
    private Integer baseExperience;
    private List<AbilityWrapper> abilities;
    @JsonProperty("held_items")
    private List<HeldItemWrapper> heldItems;
    @JsonProperty("location_area_encounters")
    private String locationAreaEncounters;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBaseExperience() {
        return baseExperience;
    }

    public void setBaseExperience(Integer baseExperience) {
        this.baseExperience = baseExperience;
    }

    public List<HeldItemWrapper> getHeldItems() {
        return heldItems;
    }

    public void setHeldItems(List<HeldItemWrapper> heldItems) {
        this.heldItems = heldItems;
    }

    public String getLocationAreaEncounters() {
        return locationAreaEncounters;
    }

    public void setLocationAreaEncounters(String locationAreaEncounters) {
        this.locationAreaEncounters = locationAreaEncounters;
    }

    public List<AbilityWrapper> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<AbilityWrapper> abilities) {
        this.abilities = abilities;
    }

    // stats removed from response DTO (not used)

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AbilityWrapper {
        private Ability ability;

        public Ability getAbility() {
            return ability;
        }

        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ability {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatWrapper {
        private Integer baseStat;
        private Stat stat;

        public Integer getBaseStat() {
            return baseStat;
        }

        public void setBaseStat(Integer baseStat) {
            this.baseStat = baseStat;
        }

        public Stat getStat() {
            return stat;
        }

        public void setStat(Stat stat) {
            this.stat = stat;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stat {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}