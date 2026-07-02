package org.example.Checks;

import org.example.Request;

public class MandatoryHeaders {

    public MandatoryHeaders() {};

    public String checkMandatoryHeaders(Request request) {

        String checkStatus = checkHost(request.getHeader("Host:"));
        if (!checkStatus.equals("200 OK")) {
            return checkStatus;
        }

        checkStatus = checkConnection(request.getHeader("Connection:"));
        if (!checkStatus.equals("200 OK")) {
            return checkStatus;
        }
        return "200 OK";
    }

    private String checkHost(String host) {

        if (host.equals("localhost:8080") || host.equals("127.0.0.1:8080")) {
            return "200 OK";
        }

        return "400 Bad Request";
    }

    private String checkConnection(String connection) {

        if (connection.equals("close") || connection.equals("keep-alive")) {
            return "200 OK";
        }

        return "400 Bad Request";
    }
}
