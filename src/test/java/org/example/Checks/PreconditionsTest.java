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
        details.put("If-Modified-Since:", "def");
        details.put("If-Unmodified-Since:", "def");
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
    }

    @Test
    void checkIfMatch() {
        // valid etag
        String statusIs = checker.checkIfMatch(request.getHeader("If-Match:"), etag);
        assertEquals(status.get200(), statusIs);

        // invalid etag
        details.put("If-Match:", "abc");
        statusIs = checker.checkIfMatch(request.getHeader("If-Match:"), etag);
        assertEquals(status.get412(), statusIs);
    }

    @Test
    void checkIfUnmodified() {
        // method is incorrect, go back to fix comparing times correctly
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
        // method is incorrect, go back to fix comparing times correctly
    }
}