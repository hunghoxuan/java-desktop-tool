package com.rs2.core.components;

import java.util.TreeMap;
import java.util.Map;

public class MyTreeMap extends TreeMap<String, String> { // using Treemap: always order by Key while HashMap is randomly
                                                         // ordered
    public String[] headers;

    public MyTreeMap(Map<String, String> values) {
        if (values == null)
            return;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            super.put(entry.getKey(), entry.getValue());
        }
    }

    public MyTreeMap() {
        super();
    }

    public String get(Object key) {
        // return super.get(key);
        String[] keys = ((String) key).split(",");
        String value = null;
        for (String key1 : keys) {
            value = super.get(key1);
            if (value != null)
                break;
        }
        return value;
    }

    public boolean containsKey(Object key) {
        // return super.containsKey(key);
        String[] keys = ((String) key).split(",");
        boolean value = false;
        for (String key1 : keys) {
            value = super.containsKey(key1);
            if (value)
                break;
        }
        return value;
    }
}