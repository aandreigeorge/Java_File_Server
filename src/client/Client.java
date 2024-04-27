package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 34522;
    private static boolean running = true;

    public static void main(String[] args) {

        System.out.println("Client started!");

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
        {
            while (running) {


                System.out.println("Sent: Give me everything you have!");
                output.writeUTF("Give me everything you have!");
                output.flush();

                String serverResponse = input.readUTF();
                System.out.println("Received: " + serverResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
