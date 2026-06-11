package org.example;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private final String protocolVersion;
    private String statusCode;
    private HashMap<String, String> headers;
    private byte[] body;

    public Response(String status) {
        this.protocolVersion = "HTTP/1.1";
        this.statusCode = status;
        this.headers = new HashMap<>();
    }

    /**
     * Sets the default values for all headers the server seeks to address. Most will be left blank, as these are
     * given from the details of a respective request, but some are set to default values since these will not change
     * for the most part
     */
    public void setDefaultHeaders() {
        // Assign. #1
        this.headers.put("Server", "ScratchServer (Version 1)");
        this.headers.put("Date", getDateTime());
        this.headers.put("Last-Modified", "");
        this.headers.put("Content-Length", "");
        this.headers.put("Content-Type", "application/octet-stream");
        this.headers.put("Connection", "");
        this.headers.put("Allow", "GET, HEAD, TRACE, OPTIONS, POST");
        // Assign. #2
        this.headers.put("ETag", "");
        this.headers.put("Location", "");
        // Assign. #3
        this.headers.put("Content-Language", "");
        this.headers.put("Content-Location", "");
        this.headers.put("Content-Encoding", "");
        this.headers.put("Transfer-Encoding", "chunked");
        this.headers.put("Alternates", "");
        this.headers.put("TCN", "");
        this.headers.put("Accept-Range", "");
        this.headers.put("Content-Range", "");
        // Assign. #4
        this.headers.put("WWW-Authenticate", "");
        this.headers.put("Authorization-Info", "");
    }

    /**
     * Sets a header to a specified value
     * @param key - header
     * @param val - value
     */
    public void setHeader(String key, String val) {
        this.headers.put(key, val);
    }

    public void setStatus(String updatedStatus) {
        this.statusCode = updatedStatus;
    }

    /**
     * Sets the body field to a desired resource's content. Will also store the size of the resource inside of headers.
     * @param b - desired resource in bytes
     */
    public void setBody(byte[] b) {
        this.body = b;
        setHeader("Content-Length", String.valueOf(this.body.length));
    }

    public String getStatus() {
        return this.statusCode;
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Returns the current map of the response headers.
     * @return - map of headers
     */
    public HashMap<String,String> getHeaderMap() {
        return this.headers;
    }

    /**
     * Returns the current date and time specified in RFC 2616
     * @return - current date time
     */
    private String getDateTime() {
        DateTimeFormatter rfcFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        return rfcFormatter.format(ZonedDateTime.now(ZoneOffset.UTC));
    }
}
