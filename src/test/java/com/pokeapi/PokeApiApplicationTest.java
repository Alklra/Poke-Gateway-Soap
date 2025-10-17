package com.pokeapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PokeApiApplicationTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void contextLoads_andRestTemplatePresent() {
        assertNotNull(ctx);
        assertTrue(ctx.containsBean("restTemplate") || ctx.getBeansOfType(RestTemplate.class).size() > 0,
                "RestTemplate bean should be present");
    }
}
