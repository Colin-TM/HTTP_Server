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

            Socket clientSocket = acceptConnection(serverSocket, HOST);

            try {
                // server-client's 5-second timed connection
                clientSocket.setSoTimeout(Integer.parseInt(props.getProperty("TIMEOUT")) * 1000);

                StatusCodes status = new StatusCodes();
                OutputStream serverWriter = clientSocket.getOutputStream();
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {

                    // parse the request for request line, headers, and body to form a request object
                    HashMap<String, String> requestDetails = RequestParser.checkRequestLine(clientReader);
                    if (requestDetails.isEmpty()) {
                        System.err.println("[ Server.java - requestDetails is empty ]");
                        break;
                    }
                    Request request = RequestParser.parseRequest(clientReader, requestDetails);

                    // check for a complete request initialization
                    if (request.getHttpMethod().isEmpty() ||
                            request.getOriginalUri().isEmpty() ||
                            request.getProtocolVersion().isEmpty() ||
                            request.getHeaders().isEmpty()) {

                        System.err.println("[ Server.java - requestInfo is null ]");
                        break;
                    }

                    // exists to coordinate the response creation process
                    RequestProcessor processor = new RequestProcessor();
                    Response response = processor.process(request, status, props);

                    break; // to be removed
                }
                break; // to be removed

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

    private static Socket acceptConnection(ServerSocket serverSocket, String HOST) throws IOException {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Connected to (" + HOST + ", " + clientSocket.getPort() + ")");
        return clientSocket;
    }
}
