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

class PartialContentTest {

    HashMap<String, String> details;
    PartialContent checker;
    Request request;
    RequestProcessor processor;
    StatusCodes status;
    int contentLength;
    String lastModified;

    @BeforeEach
    void setUp() {
        contentLength = 1936;
        details = new HashMap<>();
        details.put("Method:", "GET");
        details.put("URI:", "http://localhost:8080/a1-test/2/index.html");
        details.put("Protocol Version:", "HTTP/1.1");
        details.put("Range:", "bytes=0-59");
        details.put("If-Range:", "Sat, 20 Oct 2018 02:33:22 GMT");
        request = new Request(details);
        status = new StatusCodes();
        processor = new RequestProcessor();
        Path filePath = Paths.get("src/main/resources/docs/a1-test/2/index.html");
        lastModified = processor.getLastModified(filePath);
        checker = new PartialContent();
    }

    @Test
    void checkPartialContent() {
        // to complete...
    }

    @Test
    void parseRange() {
        // valid check -> {0, 59}
        assertArrayEquals(new int[]{0,59}, checker.parseRange(request, contentLength));
        assertFalse(checker.getRangeAdjusted());
        // invalid check -> {-1, -1}
        request.setHeader("Range:", "bytes=NOTLEGAL-ATALL");
        assertArrayEquals(new int[]{-1,-1}, checker.parseRange(request, contentLength));
        assertFalse(checker.getRangeAdjusted());
        // valid check -> {0, 1935}
        request.setHeader("Range:", "bytes=0-");
        assertArrayEquals(new int[]{0,1935}, checker.parseRange(request, contentLength));
        assertTrue(checker.getRangeAdjusted());
        // valid check -> {1000, 1935}
        request.setHeader("Range:", "bytes=-936");
        assertArrayEquals(new int[]{1000,1935}, checker.parseRange(request, contentLength));
        assertTrue(checker.getRangeAdjusted());
    }

    @Test
    void checkInRange() {
        int[] validRange = {0, 1200};
        assertEquals(status.get206(), checker.checkInRange(validRange, contentLength));
        int[] invalidRange = {0, 10000};
        assertEquals(status.get416(), checker.checkInRange(invalidRange, contentLength));
    }

    @Test
    void checkIfRange() {
        // has not been modified since
        String statusIs = checker.checkIfRange(request.getHeader("If-Range:"), lastModified);
        assertEquals(status.get206(), statusIs);
        // has been modified since
        request.setHeader("If-Range:", "Sat, 20 Oct 2018 02:33:20 GMT");
        statusIs = checker.checkIfRange(request.getHeader("If-Range:"), lastModified);
        assertEquals(status.get412(), statusIs);
        // exact time since modification
        request.setHeader("If-Range:", "Sat, 20 Oct 2018 02:33:21 GMT");
        statusIs = checker.checkIfRange(request.getHeader("If-Range:"), lastModified);
        assertEquals(status.get206(), statusIs);
    }
}