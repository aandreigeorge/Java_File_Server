package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileServer {

    private static final Path ROOT_PATH = Paths.get("C:\\Users\\andre\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data");
    private static final int PORT = 34522;
    private static boolean running = true;

    static {
        try {
            Files.createDirectories(ROOT_PATH);
        } catch (IOException e) {
            System.err.println("Error creating folder '/server/data': " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        System.out.println("Server started!");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (running) {
                handleRequest(serverSocket.accept());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket clientSocket) {

        try (
                DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())
        ) {

            String[] request = inputStream.readUTF().split(" ");
            String method = request[0].toUpperCase();
            String fileName = request[1];

            switch (method) {
                case "PUT":
                    String content = String.join(" ", Arrays.copyOfRange(request, 2, request.length));
                    if (createFile(fileName, content)) {
                        outputStream.writeUTF("200");
                    } else {
                        outputStream.writeUTF("403");
                    }
                    break;

                case "GET":
                    String fileContent = readFile(fileName);
                    if (fileContent != null) {
                        outputStream.writeUTF("200 " + fileContent);
                    } else {
                        outputStream.writeUTF("404");
                    }
                    break;

                case "DELETE":
                    if (deleteFile(fileName)) {
                        outputStream.writeUTF("200");
                    } else {
                        outputStream.writeUTF("404");
                    }
                    break;

                case "EXIT":
                    clientSocket.close();
                    running = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean createFile(String fileName, String content) {

        Path filePath = ROOT_PATH.resolve(fileName);

        try {

            File file = filePath.toFile();

            if (file.exists()) {
                return false;
            }

            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String readFile(String fileName) {

        Path filePath = ROOT_PATH.resolve(fileName);

        try {
            File file = filePath.toFile();
            if (!file.exists()) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            return content.toString().trim();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean deleteFile(String fileName) {

        Path filePath = ROOT_PATH.resolve(fileName);
        File file = filePath.toFile();

        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }
}
