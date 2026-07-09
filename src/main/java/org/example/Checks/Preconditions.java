package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Preconditions extends RequestProcessor {

    public Preconditions() {}

    public String checkPreconditions(Request request, String etag, String fileLastModified) {

        // order of precedence (w/ requirements for some)
        if (!request.getHeader("If-Match:").isEmpty())
        {
            return checkIfMatch(request.getHeader("If-Match:"), etag);
        }
        else if (request.getHeader("If-Match:").isEmpty() &&
                !request.getHeader("If-Unmodified-Since:").isEmpty())
        {
            return checkIfUnmodified(request.getHeader("If-Unmodified-Since:"), fileLastModified);
        }
        else if (!request.getHeader("If-None-Match:").isEmpty())
        {
            return checkIfNoneMatch(request.getHeader("If-None-Match:"), etag, request.getHttpMethod());
        }
        else if (request.getHeader("If-None-Match:").isEmpty() &&
                !request.getHeader("If-Modified-Since:").isEmpty() &&
                (request.getHttpMethod().equals("GET") || request.getHttpMethod().equals("HEAD")))
        {
            return checkIfModified(request.getHeader("If-Modified-Since:"), fileLastModified);
        }
        else
        {
            return "200 OK";
        }
    }

    public String checkIfMatch(String headerValue, String etag) {
        if (headerValue.equals(etag)) {
            return "200 OK";
        }

        return "412 Precondition Failed";
    }

    public String checkIfUnmodified(String headerValue, String lastModified) {
        Instant headerDate = ZonedDateTime.parse(headerValue, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        Instant fileDate = ZonedDateTime.parse(lastModified, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        if (!fileDate.isAfter(headerDate)) {
            return "200 OK";
        }

        return "412 Precondition Failed";
    }

    public String checkIfNoneMatch(String headerValue, String etag, String httpMethod) {
        if (!headerValue.equals(etag)) {
            return "200 OK";
        } else if (httpMethod.equals("GET") || httpMethod.equals("HEAD")) {
            return "304 Not Modified";
        }

        return "412 Precondition Failed";
    }

    public String checkIfModified(String headerValue, String lastModified) {
        Instant headerDate = ZonedDateTime.parse(headerValue, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        Instant fileDate = ZonedDateTime.parse(lastModified, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        if (!fileDate.isBefore(headerDate)) {
            return "200 OK";
        }

        return "304 Not Modified";
    }
}
