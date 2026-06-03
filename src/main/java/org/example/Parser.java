package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class Parser {

    public Request parseRequest(BufferedReader clientReader, OutputStream serverWriter, StatusCodes status) {

        // check for any issue straight away with the request line
        String currentLine;
        try {
            currentLine = clientReader.readLine();
        } catch (IOException e) {
            System.err.print("\n[ Parser.java - Line 16 - I/O Error ]\n");
            status.setTo400();
            return null;
        }

        // begin info gathering
        HashMap<String, String> requestDetails = initializeRequestDetails();
        //if (currentLine != null && !currentLine.isEmpty()) {
        // Pickup here...
        //}

        return new Request();
    }

    /**
     * Automatically puts all recognized parts of the request, that are recognized by this program,
     * inside the HashMap which stores this relevant information.
     */
    private HashMap<String, String> initializeRequestDetails() {
        HashMap<String, String> requestDetails = new HashMap<>();
        // request line
        requestDetails.put("Method:","");
        requestDetails.put("URI:","");
        requestDetails.put("Protocol Version:","");
        // headers
        requestDetails.put("Host:","");
        requestDetails.put("Connection:","");
        requestDetails.put("If-Modified-Since:","");
        requestDetails.put("If-Unmodified-Since:","");
        requestDetails.put("If-Match:","");
        requestDetails.put("If-None-Match:","");
        requestDetails.put("Accept:","");
        requestDetails.put("Accept-Language:","");
        requestDetails.put("Accept-Charset:","");
        requestDetails.put("Accept-Encoding:","");
        requestDetails.put("Negotiate:","");
        requestDetails.put("Range:","");
        requestDetails.put("If-Range:","");
        requestDetails.put("User-Agent:","");
        requestDetails.put("Referer:","");
        requestDetails.put("Authorization:","");
        requestDetails.put("Content-type:","");
        requestDetails.put("Content-Length:","");
        requestDetails.put("Body:","");

        return requestDetails;
    }
}
