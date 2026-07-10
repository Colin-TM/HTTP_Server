package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;
import org.example.StatusCodes;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class RequestLine extends RequestProcessor {

    private String fileExtension = "";
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
                fullPath = Paths.get(indexPath);
                this.filePath = fullPath;
                uriCode = status.get200();
            } else {
                this.filePath = fullPath;
                if (fullPath.toFile().isDirectory() && !fullPath.endsWith(File.separator)) {
                    indexPath = checkIndexPath(URI.create(uri+"/"), fullPath, status);
                    if (!indexPath.isEmpty()) {
                        Path sysPath = Paths.get(System.getProperty("user.dir")+docsRoot);
                        Path indPath = Paths.get(indexPath);
                        request.setAlteredUri(Paths.get("/" + sysPath.relativize(indPath)).toString());
                        System.out.println(request.getAlteredUri());
                        uriCode = status.get301();
                    }
                }
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

        if (uriCode.equals(status.get301())) {
            return uriCode;
        } else {
            return status.get200();
        }
    }

    public String getFileExtension() {
        return this.fileExtension;
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

        // check for index file
        if (Files.isDirectory(filePath) && uri.toString().endsWith("/")) {
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
                this.fileExtension = extension;
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
