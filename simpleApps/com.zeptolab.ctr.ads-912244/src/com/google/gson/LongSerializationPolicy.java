package com.google.gson;

public enum LongSerializationPolicy {
    DEFAULT {
        public JsonElement serialize(Number number) {
            return new JsonPrimitive(number);
        }
    },
    STRING {
        public JsonElement serialize(Long l) {
            return new JsonPrimitive(String.valueOf(l));
        }
    };

    public abstract JsonElement serialize(Long l);
}