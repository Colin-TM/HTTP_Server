package org.example.Checks;

import org.example.Request;
import org.example.StatusCodes;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MandatoryHeadersTest {

    @Test
    void checkMandatoryHeadersTest() {
        HashMap<String, String> details = new HashMap<>();
        details.put("Host:", "localhost:8080");
        details.put("Connection:", "close");
        Request request = new Request(details);
        StatusCodes status = new StatusCodes();

        // valid host, valid connection-type
        MandatoryHeaders checker = new MandatoryHeaders();
        String checkedStatus = checker.checkMandatoryHeaders(request);
        assertEquals(status.get200(), checkedStatus);

        // invalid host, valid connection-type
        request.setHeader("Host:", "http://localhost:8080");
        checkedStatus = checker.checkMandatoryHeaders(request);
        assertEquals(status.get400(), checkedStatus);
        // reset to valid host
        request.setHeader("Host:", "127.0.0.1:8080");

        // valid host, invalid connection-type
        request.setHeader("Connection:", "half-dead");
        checkedStatus = checker.checkMandatoryHeaders(request);
        assertEquals(status.get400(), checkedStatus);
        // reset to valid connection
        request.setHeader("Connection:", "keep-alive");

        // valid host, valid connection-type (alternate OKs)
        checkedStatus = checker.checkMandatoryHeaders(request);
        assertEquals(status.get200(), checkedStatus);
    }
}