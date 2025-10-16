package com.pokeapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class PojoEqualsHashMoreTest {

	@Test
	void abilityAndDetailEqualsHashcode() {
		AbilityDetail ad1 = AbilityDetail.builder().name("a").url("u").build();
		AbilityDetail ad2 = AbilityDetail.builder().name("a").url("u").build();

		// Basic equals/hashCode for AbilityDetail
		assertEquals(ad1, ad2);
		assertEquals(ad1.hashCode(), ad2.hashCode());

		// Ability uses AbilityDetail internally
		Ability a1 = Ability.builder().abilityDetail(ad1).hidden(false).slot(1).build();
		Ability a2 = Ability.builder().abilityDetail(ad2).hidden(false).slot(1).build();

		assertEquals(a1, a2);
		assertEquals(a1.hashCode(), a2.hashCode());

		// Collections should recognize equality by equals()
		List<Ability> list = new ArrayList<>();
		list.add(a1);
		assertTrue(list.contains(a2));
	}

	@Test
	void differentTypesNotEqualAndToStringNotNull() {
		AbilityDetail ad = AbilityDetail.builder().name("x").url("u").build();
		assertNotEquals(ad, "x");
		assertNotNull(ad.toString());

		Ability a = Ability.builder().abilityDetail(ad).hidden(true).slot(3).build();
		assertNotEquals(a, new Object());
		assertNotNull(a.toString());
	}

}

