package org.xw0code.android_remote_beidge.common;

import java.util.HashMap;

public class HandlerAttributes {

    public final HashMap<String,Object> attributes = new HashMap<>();

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object getAttribute(String key, Object defaultValue) {
        Object value = attributes.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public <T> T getAttribute(String key, Class<T> clazz) {
        return (T) attributes.get(key);
    }

    public <T> T getAttribute(String key, Class<T> clazz, T defaultValue) {
        Object value = attributes.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public void clear() {
        attributes.clear();
    }

    public boolean containsKey(String key) {
        return attributes.containsKey(key);
    }
}
