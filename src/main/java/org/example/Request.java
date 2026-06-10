package org.example;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private final String originalURI, httpMethod;
    private final Map<String,String> headers;

    public Request(HashMap<String, String> details) {
        this.originalURI = details.get("URI:");
        this.httpMethod = details.get("Method:");
        this.headers = details;
    };

    public String getOriginalURI() {
        return this.originalURI;
    }
    public String getHttpMethod() {
        return this.httpMethod;
    }
    public Map<String,String> getHeaders() {
        return this.headers;
    }
}
