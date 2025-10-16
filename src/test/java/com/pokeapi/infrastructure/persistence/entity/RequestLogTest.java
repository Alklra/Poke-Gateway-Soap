package com.pokeapi.infrastructure.persistence.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RequestLogTest {

    @Test
    void requestLogBasic() {
        RequestLog r = new RequestLog();
        r.setId(1L);
        r.setMethod("POST");
        r.setRequest("p");
        assertEquals(1L, r.getId());
        assertEquals("POST", r.getMethod());
        assertEquals("p", r.getRequest());
    }
}
