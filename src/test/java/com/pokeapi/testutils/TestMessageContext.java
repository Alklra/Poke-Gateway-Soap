package com.pokeapi.testutils;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.WebServiceMessage;

import java.io.IOException;
import java.io.InputStream;

/** Minimal MessageContext implementation for tests. */
public class TestMessageContext implements MessageContext {
    private final WebServiceMessage response;

    public TestMessageContext(WebServiceMessage response) {
        this.response = response;
    }

    @Override
    public WebServiceMessage getRequest() {
        return null;
    }

    @Override
    public boolean hasResponse() {
        return false;
    }

    @Override
    public WebServiceMessage getResponse() {
        return response;
    }

    @Override
    public void setResponse(WebServiceMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearResponse() {

    }

    @Override
    public void readResponse(InputStream inputStream) throws IOException {

    }

    // Adaptación a la versión actual de MessageContext: getPropertyNames devuelve String[]
    @Override
    public String[] getPropertyNames() {
        return new String[0];
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public boolean containsProperty(String name) {
        return false;
    }

    @Override
    public void setProperty(String name, Object value) {
        // no-op for tests
    }

    @Override
    public void removeProperty(String name) {
        // no-op for tests
    }
}
