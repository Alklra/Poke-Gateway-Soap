package com.pokeapi.infrastructure.adapter.rest.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonResponse {
    private Integer id;
    private String name;
    private Integer baseExperience;
    private Integer height;
    private List<AbilityWrapper> abilities;
    private List<StatWrapper> stats;

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

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<AbilityWrapper> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<AbilityWrapper> abilities) {
        this.abilities = abilities;
    }

    public List<StatWrapper> getStats() {
        return stats;
    }

    public void setStats(List<StatWrapper> stats) {
        this.stats = stats;
    }

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