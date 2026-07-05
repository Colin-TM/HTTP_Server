package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;
import org.example.StatusCodes;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class RequestLine extends RequestProcessor {

    private String mimeType = "";
    private Path filePath = null;
    public RequestLine() {}

    public String checkRequestLine(Request request, URI uri, StatusCodes status, Properties props) {
        // request line component #1
        String methodCode = checkHttpMethod(request.getHttpMethod(), status);
        if (!methodCode.equals(status.get200())) {
            return methodCode; // 501 or 400
        }

        // request line component #2 (existing URI, valid MIME, non-aggressive file search)
        String docsRoot = props.get("DOCS_ROOT").toString();
        Path requestPath = Paths.get(uri.getPath());
        Path fullPath = Paths.get(System.getProperty("user.dir")+docsRoot+requestPath);

        String uriCode = checkPathExists(fullPath, status);
        if (uriCode.equals(status.get404())) {
            String indexPath = checkIndexPath(uri, fullPath, status);
            if (!indexPath.isEmpty()) {
                this.filePath = Paths.get(indexPath);
                uriCode = status.get200();
            }
        } else { // check 200s for a directory
            this.filePath = fullPath;
            if (fullPath.toFile().isDirectory() && !fullPath.endsWith(File.separator)) {
                uriCode = status.get404();
            }
        }

        if (!uriCode.equals(status.get200())) {
            return uriCode;
        }

        uriCode = checkMimeType(fullPath, status);
        if (!uriCode.equals(status.get200())) {
            return uriCode;
        }

        // request line component #3
        String protocolCode = checkProtocolVersion(request.getProtocolVersion(), status);

        if (!protocolCode.equals(status.get200())) {
            return protocolCode;
        }

        // all checks passed
        return status.get200();
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Path getFilePath() {
        return this.filePath;
    }

    private String checkHttpMethod(String httpMethod, StatusCodes status) {

        switch (httpMethod) {
            case "GET", "HEAD", "TRACE", "POST", "PUT", "DELETE", "OPTIONS":
                return status.get200();
            case "CONNECT":
                System.err.println("[ RequestLine.java - Unimplemented HTTP Method ]");
                return status.get501();
            default:
                System.err.println("[ RequestLine.java - Unrecognized HTTP Method ]");
                return status.get400();
        }
    }

    private String checkPathExists(Path filePath, StatusCodes status) {

        if (filePath.toFile().exists() && !filePath.toFile().isDirectory()) {
            return status.get200();
        }

        return status.get404();
    }

    private String checkIndexPath(URI uri, Path filePath, StatusCodes status) {

        System.out.println(filePath);
        System.out.println(uri.toString());
        // check for index file
        if (Files.isDirectory(filePath) && uri.toString().endsWith("/")) {
            System.out.println("ok");
            filePath = Paths.get(filePath+File.separator+"index.html");

            if (filePath.toFile().exists()) {
                return filePath.toString();
            }
        }

        // if no index is found, we can safely check for an empty string
        return "";
    }

    private String checkMimeType(Path filePath, StatusCodes status) {
        ArrayList<String> extensions = getFileExtensions();
        for (String extension : extensions) {
            if (extension.equals(parseExtension(filePath.toString()))) {
                this.mimeType = extension;
                return status.get200();
            }
        }

        return status.get400();
    }

    private String checkProtocolVersion(String protocol, StatusCodes status) {

        if (protocol.equals("HTTP/1.1")) {
            return status.get200();
        } else {
            return status.get400();
        }
    }
}
