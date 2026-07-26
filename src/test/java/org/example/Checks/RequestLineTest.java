package org.example.Checks;

import org.example.Request;
import org.example.StatusCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        HashMap<String, String> details = new HashMap<>();
        details.put("Method:", "GET");
        details.put("URI:", "http://localhost:8080/a1-test/2/index.html");
        details.put("Protocol Version:", "HTTP/1.1");

        // method parameters setup
        Request request = new Request(details);
        URI uri = new URI(request.getOriginalUri());
        StatusCodes status = new StatusCodes();
        Properties props = new Properties();
        String pathToProps = System.getProperty("user.dir")+FileSystems.getDefault().getSeparator()+"server.properties";
        FileInputStream propsFile = new FileInputStream(pathToProps);
        props.load(propsFile);

        // valid method, valid path, valid protocol version
        RequestLine checker = new RequestLine();
        String checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get200(), checkedStatus);

        // unsupported method, valid path, valid protocol version
        request.setHeader("Method:", "CONNECT");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get501(), checkedStatus);

        // malformed method, valid path, valid protocol version
        request.setHeader("Method:", "get");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get400(), checkedStatus);
        // reset to valid method
        request.setHeader("Method:", "GET");

        // valid method, invalid path, valid protocol version
        request.setHeader("URI:", "http://localhost:8080/a1-test/5");
        uri = new URI(request.getOriginalUri());
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get404(), checkedStatus);

        // valid method, path to index, valid protocol version
        request.setHeader("URI:", "http://localhost:8080/a1-test/2/");
        uri = new URI(request.getOriginalUri());
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get200(), checkedStatus);

        // valid method, invalid path (to index), valid protocol version
        request.setHeader("URI:", "http://localhost:8080/a1-test/2");
        uri = new URI(request.getOriginalUri());
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get301(), checkedStatus);
        // altered URI should be set for "Location:" header
        assertEquals(Paths.get("/a1-test/2/index.html").toString(), request.getAlteredUri());

        // valid method, invalid path (MIME type), valid protocol version
        request.setHeader("URI:", "http://localhost:8080/a1-test/2/index.mp4");
        uri = new URI(request.getOriginalUri());
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get404(), checkedStatus);
        // reset to valid path
        request.setHeader("URI:", "http://localhost:8080/a1-test/2/index.html");
        uri = new URI(request.getOriginalUri());

        // valid method, valid path, invalid protocol version
        request.setHeader("Protocol Version:", "htp/10.1");
        checkedStatus = checker.checkRequestLine(request, uri, status, props);
        assertEquals(status.get505(), checkedStatus);
    }
}