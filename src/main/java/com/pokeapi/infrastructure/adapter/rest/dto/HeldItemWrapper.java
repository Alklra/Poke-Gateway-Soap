package com.pokeapi.infrastructure.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HeldItemWrapper {
    private ItemWrapper item;

    public ItemWrapper getItem() {
        return item;
    }

    public void setItem(ItemWrapper item) {
        this.item = item;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemWrapper {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}