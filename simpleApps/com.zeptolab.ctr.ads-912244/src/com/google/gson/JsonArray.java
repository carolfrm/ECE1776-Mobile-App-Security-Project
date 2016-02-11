package com.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JsonArray extends JsonElement implements Iterable {
    private final List elements;

    public JsonArray() {
        this.elements = new ArrayList();
    }

    public void add(Object obj) {
        if (obj == null) {
            obj = JsonNull.INSTANCE;
        }
        this.elements.add(obj);
    }

    public void addAll(JsonArray jsonArray) {
        this.elements.addAll(jsonArray.elements);
    }

    JsonArray deepCopy() {
        JsonArray jsonArray = new JsonArray();
        Iterator it = this.elements.iterator();
        while (it.hasNext()) {
            jsonArray.add(((JsonElement) it.next()).deepCopy());
        }
        return jsonArray;
    }

    public boolean equals(JsonArray jsonArray) {
        return jsonArray == this || (jsonArray instanceof JsonArray && jsonArray.elements.equals(this.elements));
    }

    public JsonElement get(int i) {
        return (JsonElement) this.elements.get(i);
    }

    public BigDecimal getAsBigDecimal() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsBigDecimal();
        }
        throw new IllegalStateException();
    }

    public BigInteger getAsBigInteger() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsBigInteger();
        }
        throw new IllegalStateException();
    }

    public boolean getAsBoolean() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsBoolean();
        }
        throw new IllegalStateException();
    }

    public byte getAsByte() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsByte();
        }
        throw new IllegalStateException();
    }

    public char getAsCharacter() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsCharacter();
        }
        throw new IllegalStateException();
    }

    public double getAsDouble() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsDouble();
        }
        throw new IllegalStateException();
    }

    public float getAsFloat() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsFloat();
        }
        throw new IllegalStateException();
    }

    public int getAsInt() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsInt();
        }
        throw new IllegalStateException();
    }

    public long getAsLong() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsLong();
        }
        throw new IllegalStateException();
    }

    public Number getAsNumber() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsNumber();
        }
        throw new IllegalStateException();
    }

    public short getAsShort() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsShort();
        }
        throw new IllegalStateException();
    }

    public String getAsString() {
        if (this.elements.size() == 1) {
            return ((JsonElement) this.elements.get(0)).getAsString();
        }
        throw new IllegalStateException();
    }

    public int hashCode() {
        return this.elements.hashCode();
    }

    public Iterator iterator() {
        return this.elements.iterator();
    }

    public int size() {
        return this.elements.size();
    }
}