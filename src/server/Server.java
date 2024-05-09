package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Server {

    private static final int PORT = 34522;
    private static final String DATA_LOCATION = "C:\\Users\\andre\\Desktop\\Server File\\src\\server\\data";
    static final String SERIALIZED_DATA_LOCATION = "C:\\Users\\andre\\Desktop\\Server File\\src\\server\\data\\data.ser";
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private FileSystem fileSystem;

     Server() {
        try {
            this.fileSystem = SerializingTools.deserialize(SERIALIZED_DATA_LOCATION) == null ? new FileSystem(DATA_LOCATION) :
                    SerializingTools.deserialize(SERIALIZED_DATA_LOCATION);

        } catch (Exception e) {
            System.out.println("Error while deserializing: " + e.getMessage());
            fileSystem = new FileSystem(DATA_LOCATION);
            fileSystem.loadFiles();
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (!fileSystem.isExit()) {
                this.executor.submit(new Session(serverSocket.accept(), serverSocket, this.fileSystem));
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }


        try {
            SerializingTools.serialize(this.fileSystem, SERIALIZED_DATA_LOCATION);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Error while serializing: " + e.getMessage());
        }
    }

}


