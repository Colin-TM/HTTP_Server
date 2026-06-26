package org.example.Checks;

import org.example.Request;
import org.example.StatusCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {

    @Test
    void checkRequestLine() throws URISyntaxException, IOException {
        String SLASH = File.separator;
        HashMap<String, String> details = new HashMap<>();
        details.put("Method:", "GET");
        details.put("URI:", "http://localhost:8080/a1-test/2/index.html");
        details.put("Protocol Version:", "HTTP/1.1");

        // method parameters setup
        Request request = new Request(details);
        URI uri = new URI(request.getOriginalUri());
        StatusCodes status = new StatusCodes();
        Properties props = new Properties();
        String pathToProps = System.getProperty("user.dir")+ FileSystems.getDefault().getSeparator()+"server.properties";
        FileInputStream propsFile = new FileInputStream(pathToProps);
        props.load(propsFile);

        // valid method, valid path, valid protocol version
        RequestLine checker = new RequestLine();
        String checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get200(), checkedStatus);

        // unsupported method, valid path, valid protocol version
        details.put("Method:", "CONNECTION");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get501(), checkedStatus);

        // malformed method, valid path, valid protocol version
        details.put("Method:", "get");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get400(), checkedStatus);
        // reset to valid method
        details.put("Method:", "GET");

        // valid method, invalid path, valid protocol version
        details.put("URI:", "http://localhost:8080/a1-test/5");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get404(), checkedStatus);

        // valid method, path to index, valid protocol version
        details.put("URI:", "http://localhost:8080/a1-test/2/");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get200(), checkedStatus);
        // altered URI should be set for future use
        assertEquals("http://localhost:8080/a1-test/2/index.html", request.getAlteredUri());

        // valid method, invalid path (to index), valid protocol version
        details.put("URI:", "http://localhost:8080/a1-test/2");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get404(), checkedStatus);

        // valid method, invalid path (MIME type), valid protocol version
        details.put("URI:", "http://localhost:8080/a1-test/2/index.mp4");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get400(), checkedStatus);
        // reset to valid path
        details.put("URI:", "http://localhost:8080/a1-test/2/index.html");

        details.put("Protocol Version:", "htp/10.1");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get400(), checkedStatus);
    }
}