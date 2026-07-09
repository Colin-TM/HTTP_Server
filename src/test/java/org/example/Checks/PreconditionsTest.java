package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;
import org.example.StatusCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PreconditionsTest {

    private Request request;
    private Preconditions checker;
    private HashMap<String, String> details;
    private String etag, lastModified;
    private StatusCodes status;

    @BeforeEach
    void setUp() {
        details = new HashMap<>();
        details.put("Method:", "GET");
        details.put("URI:", "http://localhost:8080/a1-test/2/index.html");
        details.put("Protocol Version:", "HTTP/1.1");
        details.put("If-Match:", "\"e5359f8ba4c1697b0d5088c8344d08d5\"");
        details.put("If-None-Match:", "abc");
        details.put("If-Modified-Since:", "Sat, 20 Oct 2018 02:33:20 GMT");   // 1 second before last modified
        details.put("If-Unmodified-Since:", "Sat, 20 Oct 2018 02:33:22 GMT"); // 1 second after last modified
        request = new Request(details);
        status = new StatusCodes();
        RequestProcessor processor = new RequestProcessor();

        Path filePath = Paths.get("src/main/resources/docs/a1-test/2/index.html");
        processor.setFilePath(filePath);
        processor.setEtag(status);
        etag = processor.getEtag();
        lastModified = processor.getLastModified(filePath);
        checker = new Preconditions();
    }

    @Test
    void checkPreconditions() {
        // All test cases should return 200 since we are working with valid data in setUp().
        // Data returning non-200 responses are checked in individual method tests below this one.

        // "If-Match" is not empty (does not matter if others are filled)
        String statusIs = checker.checkPreconditions(request, etag, lastModified);
        assertEquals(status.get200(), statusIs);

        // "If-Match" is empty & "If-Unmodified-Since" is not empty
        request.setHeader("If-Match:", "");
        statusIs = checker.checkPreconditions(request, etag, lastModified);
        assertEquals(status.get200(), statusIs);

        // "If-Match" & "If-Unmodified-Since" are empty & "If-None-Match" is not empty
        request.setHeader("If-Unmodified-Since:", "");
        statusIs = checker.checkPreconditions(request, etag, lastModified);
        assertEquals(status.get200(), statusIs);

        // All other preconditions are empty & "If-Modified-Since" is not empty & HTTP method is "GET" or "HEAD"
        request.setHeader("If-None-Match:", "");
        statusIs = checker.checkPreconditions(request, etag, lastModified);
        assertEquals(status.get200(), statusIs);

        // All preconditions are empty = 200 OK
        request.setHeader("If-Modified-Since:", "");
        statusIs = checker.checkPreconditions(request, etag, lastModified);
        assertEquals(status.get200(), statusIs);
    }

    @Test
    void checkIfMatch() {
        // valid etag
        String statusIs = checker.checkIfMatch(request.getHeader("If-Match:"), etag);
        assertEquals(status.get200(), statusIs);

        // invalid etag
        statusIs = checker.checkIfMatch(request.getHeader("If-None-Match:"), etag);
        assertEquals(status.get412(), statusIs);
    }

    @Test
    void checkIfUnmodified() {
        // has not been modified since
        String statusIs = checker.checkIfUnmodified(request.getHeader("If-Unmodified-Since:"), lastModified);
        assertEquals(status.get200(), statusIs);

        // has been modified since
        statusIs = checker.checkIfUnmodified(request.getHeader("If-Modified-Since:"), lastModified);
        assertEquals(status.get412(), statusIs);

        // exact time since modification
        statusIs = checker.checkIfUnmodified("Sat, 20 Oct 2018 02:33:21 GMT", lastModified);
        assertEquals(status.get200(), statusIs);
    }

    @Test
    void checkIfNoneMatch() {
        // will not match = 200 status
        String statusIs = checker.checkIfNoneMatch(request.getHeader("If-None-Match:"), etag, request.getHttpMethod());
        assertEquals(status.get200(), statusIs);

        // will match + GET/HEAD = 304 status
        etag = "abc";
        statusIs = checker.checkIfNoneMatch(request.getHeader("If-None-Match:"), etag, request.getHttpMethod());
        assertEquals(status.get304(), statusIs);

        // will match + non-GET/non-HEAD = 412 status
        details.put("Method:", "TRACE");
        statusIs = checker.checkIfNoneMatch(request.getHeader("If-None-Match:"), etag, request.getHttpMethod());
        assertEquals(status.get412(), statusIs);
    }

    @Test
    void checkIfModified() {
        // has been modified since
        String statusIs = checker.checkIfModified(request.getHeader("If-Modified-Since:"), lastModified);
        assertEquals(status.get200(), statusIs);

        // has not been modified since
        statusIs = checker.checkIfModified(request.getHeader("If-Unmodified-Since:"), lastModified);
        assertEquals(status.get304(), statusIs);

        // exact time since modification
        statusIs = checker.checkIfModified("Sat, 20 Oct 2018 02:33:21 GMT", lastModified);
        assertEquals(status.get200(), statusIs);
    }
}