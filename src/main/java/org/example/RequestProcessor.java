package org.example;

public class RequestProcessor {

    public RequestProcessor() {}

    public void process(Request request, StatusCodes status) {
        Response response = createResponse();
        // continue to stage creations...
        // -> HTTP Method
        // -> URI
        // after the two above are complete, begin routing to file and writing a response
    }

    private Response createResponse() {
        Response response = new Response("200 OK");
        response.setDefaultHeaders();
        return response;
    }
}
