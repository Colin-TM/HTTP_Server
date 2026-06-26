package org.example;

import org.example.Checks.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class RequestProcessor {

    public RequestProcessor() {}

    public Response process(Request request, StatusCodes status, Properties props) {
        // will need URI as an object throughout the process
        URI uri = getURI(request.getOriginalUri());
        Response response = createResponse();

        // all checks
        RequestLine requestLine = new RequestLine();
        requestLine.checkRequestLine(request, uri, status, props);


        return response;
    }

    public URI getURI(String requestUri) {
        try {
            return new URI(requestUri);
        } catch (URISyntaxException e) {
            System.err.println("[ RequestLine.java - Invalid URI pattern ]");
            return null;
        }
    }

    public HashMap<String, String> getMimeMap() {
        HashMap<String, String> mimes = new HashMap<>();
        mimes.put("octet-stream", "application/octet-stream");
        mimes.put("gif", "image/gif");
        mimes.put("html", "text/html");
        mimes.put("http", "message/http");
        mimes.put("jpeg", "image/jpeg");
        mimes.put("pdf", "application/pdf");
        mimes.put("txt", "text/plain");
        mimes.put("png", "image/png");
        mimes.put("ppt", "application/vnd.ms-powerpoint");
        mimes.put("word", "application/vnd.ms-word");
        mimes.put("xml", "text/xml");
        return mimes;
    }

    public ArrayList<String> getFileExtensions() {
        ArrayList<String> extensions = new ArrayList<>();
        extensions.add("octet-stream");
        extensions.add("gif");
        extensions.add("html");
        extensions.add("http");
        extensions.add("jpeg");
        extensions.add("pdf");
        extensions.add("txt");
        extensions.add("png");
        extensions.add("ppt");
        extensions.add("word");
        extensions.add("xml");
        return extensions;
    }

    protected String parseExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf(".");
        return filePath.substring(dotIndex);
    }

    private Response createResponse() {
        Response response = new Response("200 OK");
        response.setDefaultHeaders();
        return response;
    }
}
