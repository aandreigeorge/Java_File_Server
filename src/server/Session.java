package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static server.SerializingTools.serialize;

public class Session implements Runnable {

    private final Socket clientSocket;
    private final ServerSocket serverSocket;
    private final FileSystem fileSystem;

    public Session(Socket clientSocket, ServerSocket serverSocket, FileSystem fileSystem) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
        this.fileSystem = fileSystem;
    }

    @Override
    public void run() {
        try (
                DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            int inputCommand = inputStream.readInt();

            switch (inputCommand) {

                case 0:
                    this.fileSystem.setExit(true);
                    serialize(this.fileSystem, Server.SERIALIZED_DATA_LOCATION);
                    clientSocket.close();
                    serverSocket.close();
                    break;
                case 1:
                    fileSystem.GET(inputStream, outputStream);
                    run();
                    break;
                case 2:
                    fileSystem.PUT(inputStream, outputStream);
                    run();
                    break;
                case 3:
                    fileSystem.DELETE(inputStream, outputStream);
                    break;
                default:
                    System.out.println("Unrecognized command: " + inputCommand);
            }
        } catch (Exception e) {
            System.out.println("Error while processing command: " + e.getMessage());
        }
    }
}

