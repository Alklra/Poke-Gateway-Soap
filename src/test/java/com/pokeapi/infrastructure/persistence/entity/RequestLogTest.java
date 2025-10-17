package com.pokeapi.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class RequestLogTest {

    @Test
    void defaultConstructorSetsTimestampAndGettersSettersWork() {
        RequestLog r = new RequestLog();
        assertNotNull(r.getTimestamp(), "timestamp debe inicializarse en el constructor por defecto");

        LocalDateTime now = LocalDateTime.now();
        // Allow small clock skew
        assertFalse(r.getTimestamp().isAfter(now.plusSeconds(5)), "timestamp no debe estar en el futuro");

        r.setIpAddress("127.0.0.1");
        r.setMethod("GET");
        r.setRequest("{\"q\":1}");
        r.setResponse("{\"ok\":true}");
        r.setDuration(123L);
        r.setId(10L);

        assertEquals(Long.valueOf(10L), r.getId());
        assertEquals("127.0.0.1", r.getIpAddress());
        assertEquals("GET", r.getMethod());
        assertEquals("{\"q\":1}", r.getRequest());
        assertEquals("{\"ok\":true}", r.getResponse());
        assertEquals(Long.valueOf(123L), r.getDuration());
    }

    @Test
    void constructorWithIpAndMethodSetsValues() {
        RequestLog r = new RequestLog("192.168.1.2", "POST");
        assertEquals("192.168.1.2", r.getIpAddress());
        assertEquals("POST", r.getMethod());
        assertNotNull(r.getTimestamp(), "timestamp debe inicializarse también en este constructor");
    }

    @Test
    void timestampSetterOverridesValue() {
        RequestLog r = new RequestLog();
        LocalDateTime custom = LocalDateTime.of(2000, 1, 1, 0, 0);
        r.setTimestamp(custom);
        assertEquals(custom, r.getTimestamp());
    }

}
