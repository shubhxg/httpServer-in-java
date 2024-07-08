import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

// steps: 
// 1. Create a server socket to listen on the port
// 2. make it listen on the port while its running and accept the connections
// 3. Input data from the client
// 4. Get the path from the header 
// 5. Type of connection (Get request)
// 6. Send back response header and body.
// 7. Close the socket

public class Main {
    private static final int PORT = 8080;
    private static String htmlCode = "<html><body><h1>Hello World</h1></body></html>";
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                try {
                    // input stream to input data from the client
                    InputStream input = clientSocket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    // reading request from the client until the line is empty
                    String line = reader.readLine();
                    String path = "";
                    while(!line.isEmpty()) {
                        if (line.startsWith("GET /")) {
                            path = line.split(" ")[1];
                            break;
                        }
                        line = reader.readLine();
                    }
                    
                    // output stream to send back data to client
                    OutputStream output = clientSocket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    // sending back response headers
                    writer.println("HTTP/1.1 200 OK");
                    writer.println("Content-Type: text/html; charset=UTF-8");
                    writer.println("");

                    // sending back response body
                    if ("/helloworld".equals(path)) {
                        writer.println(htmlCode);
                    }
                    
                    // closing writer
                    writer.close();

                    // closing socket
                    clientSocket.close();
                    
                } catch (IOException e) {
                    System.err.println("Error" + e.getMessage());
                    e.getStackTrace();
                }
            }

        }  catch (IOException e) {
            System.err.println("Error" + e.getMessage());
            e.getStackTrace(); // a better error throwing method
        }
    }
}
