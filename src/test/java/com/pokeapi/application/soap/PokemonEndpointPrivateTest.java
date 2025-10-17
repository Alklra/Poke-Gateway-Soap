package com.pokeapi.application.soap;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PokemonEndpointPrivateTest {

    @Test
    void setPropertyIfPossibleHandlesMissingFieldAndPrimitive() throws Exception {
        // Create endpoint with a null service (we won't call public methods)
        var endpoint = new PokemonEndpoint(null);

        // Access private method
        Method m = PokemonEndpoint.class.getDeclaredMethod("setPropertyIfPossible", Object.class, String.class, Object.class);
        m.setAccessible(true);

        // Case 1: target without the field -> NoSuchFieldException should be swallowed
        Object target = new Object();
        assertDoesNotThrow(() -> m.invoke(endpoint, target, "nonexistent", "value"));

        // Case 2: target with a primitive field - create a simple holder
        class Holder { public int x = 5; }
        Holder h = new Holder();
        // setting null for primitive should be ignored (method returns silently)
        assertDoesNotThrow(() -> m.invoke(endpoint, h, "x", null));
        // value should stay as default
        assertEquals(5, h.x);
    }
}
