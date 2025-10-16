package com.pokeapi.domain.model;

import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pokemon {
    private Long id;
    private String name;
    private List<String> abilities;
    private Integer baseExperience;
    private List<HeldItem> heldItems;
    private String locationAreaEncounters;
}
