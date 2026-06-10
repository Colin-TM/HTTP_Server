package org.example;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Properties;
import java.net.*;

public class Server {

    static void main(String[] args) throws IOException {

        Properties props = loadPropertiesFile();
        final String HOST = props.getProperty("HOST");
        final int PORT = Integer.parseInt(props.getProperty("PORT"));
        ServerSocket serverSocket = new ServerSocket();
        listenForConnection(serverSocket, HOST, PORT);

        while (true) {

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to (" + HOST + ", " + clientSocket.getPort() + ")");

            try {
                // server-client's 5-second timed connection
                clientSocket.setSoTimeout(Integer.parseInt(props.getProperty("TIMEOUT")) * 1000);

                OutputStream serverWriter = clientSocket.getOutputStream();
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {

                    // all requests begin with a 200 status code before any checks take place
                    StatusCodes status = new StatusCodes();
                    status.setTo200();

                    // parse the request for request line, headers, and body to form a request object
                    HashMap<String, String> requestDetails = Parser.checkRequestLine(clientReader, status);
                    if (requestDetails.isEmpty()) {
                        System.out.println("[ Server.java - requestDetails is empty ]");
                        // return a response object here & change status code
                    }
                    Request request = Parser.parseRequest(clientReader, status, requestDetails);

                    // check for a complete request initialization
                    if (request.getHttpMethod().isEmpty() ||
                            request.getOriginalURI().isEmpty() ||
                            request.getHeaders().isEmpty()) {

                        System.out.println("[ Server.java - requestInfo is null ]");
                        break;
                    }

                    // Create a coordinator class to begin response creation...
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
                // create new response object for a timed out response
            } catch (IOException e) {
                clientSocket.close();
            }
        }
    }

    public static Properties loadPropertiesFile() throws IOException {
        Properties props = new Properties();
        String pathToProps = System.getProperty("user.dir")+FileSystems.getDefault().getSeparator()+"server.properties";
        FileInputStream propsFile = new FileInputStream(pathToProps);
        props.load(propsFile);
        return props;
    }

    private static void listenForConnection(ServerSocket serverSocket, String HOST, int PORT) throws IOException {
        serverSocket.bind(new InetSocketAddress(HOST, PORT));
        System.out.println(
                "Listening on " + HOST +
                ":" + PORT + " for HTTP connections..."
        );
    }
}
