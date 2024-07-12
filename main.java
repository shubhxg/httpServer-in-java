import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class main {
    private static final int PORT = 8000;
    private static String htmlCode = "<html><body><h1>Hello World</h1></body></html>";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                // method to handle client requests
                handleClientRequest(clientSocket);
            }

        } catch (IOException e) {
            System.err.println("Error" + e.getMessage());
            e.getStackTrace(); // a better error throwing method
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String path = getRequestPath(reader);
            sendResponse(writer, path);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private static String getRequestPath(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String path = "";
        while (line != null && !line.isEmpty()) {
            if (line.startsWith("GET /")) {
                path = line.split(" ")[1];
                break;
            }
            line = reader.readLine();
        }
        return path;
    }

    private static void sendResponse(PrintWriter writer, String path) {
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println();

        if ("/helloworld".equals(path)) {
            writer.println(htmlCode);
        } else if (path.startsWith("/file/")) {
            String fileName = path.substring(6);
            String fileContent = readFileContent(fileName);
            writer.println("<html><body><pre>" + fileContent + "</pre></body></html>");
        } else {
            writer.println("<html><body><h1>404 Not Found</h1></body></html>");
        }
    }

    private static String readFileContent(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Unable to read file " + fileName;
        }
    }
}
