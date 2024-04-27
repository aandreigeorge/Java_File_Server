package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread {

    private final Socket CLIENT_SOCKET;

    public Session(Socket socket) {
        this.CLIENT_SOCKET = socket;
    }

    @Override
    public void run() {

        try (DataInputStream input = new DataInputStream(CLIENT_SOCKET.getInputStream());
             DataOutputStream output = new DataOutputStream(CLIENT_SOCKET.getOutputStream())
        ) {

            String inputLine = input.readUTF();

            if (inputLine.equalsIgnoreCase("Give me everything you have!")) {
                System.out.println("Received: " + inputLine);
                System.out.println("Sent: All files were sent!");
                output.writeUTF("All files were sent!");
            }

            CLIENT_SOCKET.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

