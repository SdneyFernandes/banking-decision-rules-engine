package com.sdney.rulesengine.domain.evaluations;

import java.util.Map;

public class Facts {

    private final Map<String, Object> values;

    public Facts(Map<String, Object> values) {

        if(values == null) {
            throw new IllegalArgumentException("Facts cannot ne null");
        }

        if(values.isEmpty()) {
            throw new IllegalArgumentException("Facts cannot ne empty");
        }

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key == null || key.isBlank()) {
                throw new IllegalArgumentException("Facts key is required");
            }

            if(value == null) {
                throw new IllegalArgumentException("Facts value is required");
            }
        }
        this.values = Map.copyOf(values);
    }

    public Object get(String factKey) {

        if(factKey == null || factKey.isBlank()) {
            throw new IllegalArgumentException("Facts key is required");
        }

        if(!values.containsKey(factKey)) {
            throw new IllegalStateException("Fact not found: " + factKey);
        }

        return values.get(factKey);
    }



}
