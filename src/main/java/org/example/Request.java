package org.example;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private String originalUri, httpMethod, protocolVersion;
    private String alteredUri = "";
    private Map<String,String> headers;

    public Request(HashMap<String, String> details) {
        this.originalUri = details.get("URI:");
        this.httpMethod = details.get("Method:");
        this.protocolVersion = details.get("Protocol Version:");
        this.headers = details;
    };

    public void setHeader(String key, String val) { this.headers.put(key, val); }
    public void setAlteredUri(String newUri) { this.alteredUri = newUri; }
    public String getAlteredUri() { return this.alteredUri; }
    public String getOriginalUri() {
        return this.headers.get("URI:");
    }
    public String getHttpMethod() {
        return this.headers.get("Method:");
    }
    public String getProtocolVersion() { return this.headers.get("Protocol Version:"); }
    public Map<String,String> getHeaders() { return this.headers; }
}
