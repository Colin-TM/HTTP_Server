package org.example;
import java.io.*;
import java.nio.file.FileSystems;
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

                // begin timer
                clientSocket.setSoTimeout(Integer.parseInt(props.getProperty("TIMEOUT")) * 1000);

                // initialize I/O streams
                OutputStream serverWriter = clientSocket.getOutputStream();
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // keeps connection alive, so long the server receives requests
                while (true) {

                    StatusCodes status = new StatusCodes();

                    // RequestParser.java
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
