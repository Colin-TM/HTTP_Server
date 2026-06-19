package org.example;

import org.example.Checks.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class RequestProcessor {

    public RequestProcessor() {}

    public void process(Request request, StatusCodes status, Properties props) {
        // will need URI as an object throughout the process
        URI uri = getURI(request.getOriginalUri());
        Response response = createResponse();

        // all checks
        RequestLine requestLine = new RequestLine();
        requestLine.checkRequestLine(request, uri, status, props);
    }

    public URI getURI(String requestUri) {
        try {
            return new URI(requestUri);
        } catch (URISyntaxException e) {
            System.err.println("[ RequestLine.java - Invalid URI pattern ]");
            return null;
        }
    }

    private Response createResponse() {
        Response response = new Response("200 OK");
        response.setDefaultHeaders();
        return response;
    }
}
