package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class Parser {
    /**
     * Checks the incoming request using the BufferedReader's readLine() method. The method only aims to validate the
     * request line and will return null if there are any issues.
     * @param clientReader - the request's BufferedReader object
     * @param status - StatusCodes object to update the current status
     * @return - filled HashMap || empty HashMap (if error(s) occur)
     */
    public static HashMap<String, String> checkRequestLine(BufferedReader clientReader, StatusCodes status) {

        // check for emptiness or some other I/O error
        String currentLine;
        try {
            currentLine = clientReader.readLine();
        } catch (IOException e) {
            System.err.println("[ Parser.java - I/O Error ]");
            status.setTo400();
            return new HashMap<>();
        }

        // initialize map to hold request line details and check if all needed details were available
        String[] requestLine = currentLine.split(" ");
        HashMap<String, String> details = new HashMap<>();
        try {

            if (requestLine[0] == null) {
                System.err.println("[ Parser.java - Index Empty ]");
                status.setTo400();
                return new HashMap<>();
            }

            details.put("Method:", requestLine[0]);
            details.put("URI:", requestLine[1]);
            details.put("Protocol Version:", requestLine[2]);

        } catch (IndexOutOfBoundsException e) {
            System.err.println("[ Parser.java - Array Overflow #1 ]");
            status.setTo400();
            return new HashMap<>();
        }

        // add the remaining potential headers as keys
        putHeaders(details);

        return details;
    }

    /**
     * Using the request's details HashMap, this method parses the request line-by-line looking for suitable headers to
     * be matched and stored.
     * @param clientReader - the request's BufferedReader object
     * @param status - StatusCodes object to update the current status
     * @param details - the HashMap being used to store the request's components
     * @return - a new Request object
     * @throws IOException - should not be thrown, considering the above method
     */
    public static Request parseRequest(BufferedReader clientReader, StatusCodes status, HashMap<String,String> details)
            throws IOException {

        String currentLine;
        while ((currentLine = clientReader.readLine()) != null && !currentLine.isEmpty()) {
            try {
                // break the current line into parts
                String[] lineParts = currentLine.split("");

                // headers
                if (details.containsKey(lineParts[0])) {
                    // initialize capacity to hold the entire line after the colon of the header
                    StringBuilder headerBuild = new StringBuilder(currentLine.length() - lineParts[0].length());
                    for (int i = 1; i < lineParts.length; i++) {
                        headerBuild.append(lineParts[i]);
                    }
                    // put the matched header's details inside respective place in map
                    details.replace(lineParts[0], headerBuild.toString());

                    if (lineParts[0].equals("Content-Length:")) {
                        break;
                    }
                }

            } catch (IndexOutOfBoundsException e) {
                System.err.println("[ Parser.java - Array Overflow #2 ]");
                status.setTo400();
                return new Request(new HashMap<>());
            }
        }

        // body (only if the Content-Length header is found)
        if (!details.get("Content-Length:").isEmpty()) {
            // establish a length to read to and assure the provided is greater than 0
            int contentLength = Integer.parseInt(details.get("Content-Length:")) + 1;
            if (contentLength <= 0) {
                System.out.println("[ Parser.java - Supplied Content Length is LESS than 0 ]");
                status.setTo400();
            }

            StringBuilder bodyBuilder = new StringBuilder(contentLength);
            char[] collectedChars = new char[contentLength];
            int readCounter = 0;

            while (readCounter < contentLength) {
                int readThis = clientReader.read(collectedChars, readCounter, (contentLength - readCounter));
                if (readThis == -1) { break; }
                readCounter += readThis;
            }
            bodyBuilder.append(collectedChars);
            details.put("Body:", bodyBuilder.toString());
        }

        if (details.get("Connection:").isEmpty()) {
            details.replace("Connection:", "keep-alive");
        }

        return new Request(details);
    }

    /**
     * Automatically puts all recognized headers of the request, that are recognized by this program,
     * inside the HashMap which stores this relevant information to be later used for easy-access.
     */
    private static void putHeaders(HashMap<String, String> requestDetails) {
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
    }
}
