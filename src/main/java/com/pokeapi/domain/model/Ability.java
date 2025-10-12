package com.pokeapi.domain.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ability {
    private AbilityDetail ability;
    private boolean is_hidden;
    private Integer slot;
}