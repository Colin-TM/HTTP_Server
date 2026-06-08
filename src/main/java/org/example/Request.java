package org.example;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private Map<String,String> info;
    private final String originalURI;

    public Request(HashMap<String, String> details) {
        this.info = details;
        this.originalURI = details.get("URI:");
    };

    public Map<String,String> getRequestInfo() {
        return this.info;
    }

    public String getOriginalURI() {
        return this.originalURI;
    }
}
