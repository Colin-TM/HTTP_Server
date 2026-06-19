package org.example.Checks;

import org.example.Request;
import org.example.StatusCodes;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * NEW IDEA - set attributes for request line to reflect the codes returned for each check
 *          - this way, when there is no error, we simply return true and carry on
 *          - else, when there is error, we can simply grab the code from the method directly with get/set
 */
public class RequestLine {

    public RequestLine() {}

    public boolean checkRequestLine(Request request, URI uri, StatusCodes status, Properties props) {
        String methodCode = checkHttpMethod(request.getHttpMethod(), status);
        String uriCode = checkUriPath(uri, status, props);
        String protocolCode = checkProtocolVersion(request.getProtocolVersion(), status);

        if (methodCode.equals(status.get200()) &&
                uriCode.equals(status.get200()) &&
                protocolCode.equals(status.get200())) {
            return true;
        } else {
            // find status code of the FIRST method that was not 200
            return false;
        }
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

    private String checkUriPath(URI uri, StatusCodes status, Properties props) {

        String docsRoot = props.get("DOCS_ROOT").toString();
        Path requestPath = Paths.get(uri.getPath());
        Path responsePath = Paths.get(docsRoot+requestPath);

        //String fileName = filePath.toFile().getName();
        //System.out.println("URI: " + uri.toString());
        //System.out.println("Path: " + filePath);
        //System.out.println("File: " + fileName);
        //System.out.println("Response Path: " + docsRoot+filePath);

        // check path existence
        if (responsePath.toFile().exists()) {
            return status.get200();
        }

        // check if ending in slash - if so, find index.html
        if (responsePath.endsWith(File.separator)) {
            responsePath = Paths.get(responsePath+File.separator+"index.html");

            if (responsePath.toFile().exists()) {
                // if the path exists, we must update Request's alteredUri field
                // return 200
            }
        }

        return status.get404();
    }

    private String checkProtocolVersion(String protocol, StatusCodes status) {

        if (protocol.equals("HTTP/1.1")) {
            return status.get200();
        } else {
            return status.get400();
        }
    }

}
