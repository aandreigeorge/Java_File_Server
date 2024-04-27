package server;

import java.io.IOException;
import java.net.ServerSocket;

public class EchoServer {

    private static final int PORT = 34522;
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("Server started!");

        try (ServerSocket server = new ServerSocket(PORT)) {

            while (running) {
                Session session = new Session(server.accept());
                session.start();
                running = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
