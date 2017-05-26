package org.aviran.woodpecker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aviran on 30/05/17.
 */

public class WoodpeckerSettings {
    private String baseURL;
    private Map<String, String> headers;

    public WoodpeckerSettings(String baseURL) {
        this.baseURL = baseURL;
        headers = new HashMap<>();
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void removeHeader(String name) {
        if(headers.containsKey(name)) {
            headers.remove(name);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
