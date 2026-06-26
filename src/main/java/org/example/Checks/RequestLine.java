package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;
import org.example.StatusCodes;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class RequestLine extends RequestProcessor {

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
        Path fullPath = Paths.get(docsRoot+requestPath);

        String uriCode = checkPathExists(fullPath, status);
        if (uriCode.equals(status.get404())) {
            String indexPath = checkIndexPath(fullPath, status);
            if (!indexPath.isEmpty()) {
                request.setAlteredUri(indexPath);
                uriCode = status.get200();
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

    private String checkPathTraversal(Path filePath, StatusCodes status) {
        // continue here!
        return "";
    }

    private String checkPathExists(Path filePath, StatusCodes status) {

        if (filePath.toFile().exists()) {
            return status.get200();
        }

        return status.get404();
    }

    private String checkIndexPath(Path filePath, StatusCodes status) {

        // check for index file
        if (filePath.endsWith(File.separator)) {
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
