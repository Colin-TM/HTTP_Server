package org.example;

import org.example.Checks.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Properties;

public class RequestProcessor {

    Path filePath;
    String etag;
    public RequestProcessor() {}

    public Response process(Request request, StatusCodes status, Properties props) {

        URI uri = getURI(request.getOriginalUri());
        Response response = createResponse();
        HashMap<String, String> responseHeaders = response.getHeaderMap();

        // all checks (processed chronologically by importance)
        RequestLine requestLine = new RequestLine();
        String statusIs = requestLine.checkRequestLine(request, uri, status, props);

        if (statusIs.equals(status.get200())) {
            setFilePath(requestLine.getFilePath());
            response.setHeader("Content-Type", findMime(requestLine.getFileExtension()));
            response.setHeader("Content-Length", String.valueOf(getFilePath().toFile().length()));
            response.setHeader("Last-Modified", getLastModified(this.getFilePath()));
            if (!request.getAlteredUri().isEmpty()) {
                response.setHeader("Location", request.getAlteredUri());
            }
        } else if (statusIs.equals(status.get301())) {
            // Location header must be set here
            response.setHeader("Location", request.getAlteredUri());
            // HTML page must be generated & path must be set here
            response.setStatus(status.get301());
        }

        MandatoryHeaders mandatoryHeaders = new MandatoryHeaders();
        statusIs = mandatoryHeaders.checkMandatoryHeaders(request);
        if (statusIs.equals(status.get200())) {
            response.setHeader("Connection", request.getHeader("Connection:"));
        }

        setEtag(status);
        response.setHeader("ETag", getEtag());
        Preconditions preconditions = new Preconditions();
        statusIs = preconditions.checkPreconditions(request, getEtag(), responseHeaders.get("Last-Modified:"));
        // return a response object for non-200 status codes here after preconditions check

        // partial content checks here!
        PartialContent partialContent = new PartialContent();
        statusIs = partialContent.checkPartialContent(request);

        // 302s do not return automatically, so must check
        if (!response.getStatus().equals(status.get301())) {
            response.setStatus(statusIs);
            response.setHeader("Location", "");
        }
        return response;
    }

    public void setEtag(StatusCodes status) {
        try {
            if (!generateEtag(Files.readAllBytes(getFilePath())).equals(status.get500())) {
                this.etag = generateEtag(Files.readAllBytes(getFilePath()));
            }
        } catch (IOException e) {
            //throw new RuntimeException(e);
            System.err.println("[ RequestProcessor - Failed to generate Etag ]");
        }
    }

    public void setFilePath(Path fullPath) {
        this.filePath = fullPath;
    }

    public Path getFilePath() {
        return this.filePath;
    }

    public String getEtag() {
        return this.etag;
    }

    public URI getURI(String requestUri) {
        try {
            return new URI(requestUri);
        } catch (URISyntaxException e) {
            System.err.println("[ RequestLine.java - Invalid URI pattern ]");
            return null;
        }
    }

    public String findMime(String fileExtension) {
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

        if (mimes.containsKey(fileExtension)) {
            return mimes.get(fileExtension);
        } else {
            return mimes.get("octet-stream");
        }
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
        int dotIndex = filePath.lastIndexOf(".") + 1;
        return filePath.substring(dotIndex);
    }

    public String getLastModified(Path filePath) {
        FileTime fileTime = null;
        try {
            fileTime = Files.getLastModifiedTime(filePath);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            System.err.println("[ RequestProcessor - Conversion to RFC Date-Time failed ]");
        }
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        assert fileTime != null;
        return fileTime.toInstant().atZone(ZoneOffset.UTC).format(dateTimeFormat);
    }

    protected String generateEtag(byte[] resource) {
        String etag = "";
        try {
            MessageDigest computeMD5 = MessageDigest.getInstance("MD5");
            byte[] etagInBytes = computeMD5.digest(resource);
            etag = "\"" + HexFormat.of().formatHex(etagInBytes) + "\"";
        } catch (NoSuchAlgorithmException e) {
            //throw new RuntimeException(e);
            return "500 Internal Server Error";
        }

        return etag;
    }

    public String generateHtmlContent(String status, String message) {
        return String.format(
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <title>%s</title>
                </head>
                <body>
                    <h3>%s</h3>
                    <p>%s</p>
                </body>
                </html>
                """, status, status, message);
    }

    public void generateHtmlPage(String fileName, String htmlContent) {
        try (FileWriter fileWriter = new FileWriter(fileName); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(htmlContent);
        } catch (IOException e) {
            System.err.println("[ RequestProcessor - Failed to generate HTML page ]");
        }
    }

    private Response createResponse() {
        Response response = new Response("200 OK");
        response.setDefaultHeaders();
        return response;
    }
}
