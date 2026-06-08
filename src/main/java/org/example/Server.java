package org.example;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Properties;
import java.net.*;

public class Server {

    public static Properties loadPropertiesFile() throws IOException {

        Properties props = new Properties();
        String pathToProps = System.getProperty("user.dir")+FileSystems.getDefault().getSeparator()+"server.properties";
        FileInputStream propsFile = new FileInputStream(pathToProps);
        props.load(propsFile);
        return props;

    }

    static void main(String[] args) throws IOException {

        // create server's socket using vals from properties file
        Properties props = loadPropertiesFile();
        String HOST = props.getProperty("HOST");
        int PORT = Integer.parseInt(props.getProperty("PORT"));

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(HOST, PORT));
        System.out.println(
                "Listening on " + HOST +
                ":" + PORT + " for HTTP connections..."
        );

        // create connection between server and client
        while (true) {

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to (" + HOST + ", " + clientSocket.getPort() + ")");

            try { // server-client's 5-second timed connection
                clientSocket.setSoTimeout(Integer.parseInt(props.getProperty("TIMEOUT")) * 1000);

                // initialize I/O streams
                OutputStream serverWriter = clientSocket.getOutputStream();
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // keeps connection alive, so long the server receives requests
                while (true) {

                    // initialize status object to 200
                    StatusCodes status = new StatusCodes();
                    status.setTo200();

                    // parse the request for request line, headers, and body to form a request object
                    HashMap<String, String> requestDetails = Parser.checkRequestLine(clientReader, status);
                    if (requestDetails.isEmpty()) {
                        System.out.println("[ Server.java - requestDetails is empty ]");
                        // return a response object here & change status code
                    }
                    Request request = Parser.parseRequest(clientReader, status, requestDetails);

                    // check for a complete request initialization (empty or null)
                    if (request.getRequestInfo().isEmpty()) {
                        System.out.println("[ Server.java - Request object is empty ]");
                        // return a response object here & change status code
                    } else if (request.getRequestInfo() == null) {
                        System.out.println("[ Server.java - requestInfo is null ]");
                        break;
                    }

                    // continue to ResponseHandler()...
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
                // create new response object for a timed out response
            } catch (IOException e) {
                clientSocket.close();
            }
        }
    }
}
