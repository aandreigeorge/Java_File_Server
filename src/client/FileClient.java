package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class FileClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 34522;

    public static void main(String[] args) {

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                Scanner scanner = new Scanner(System.in)
        ) {

            while (true) {

                System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file, exit): ");
                String action = scanner.nextLine().toUpperCase();

                switch (action) {

                    case "1": // GET
                        System.out.println("Enter filename: ");
                        String fileToGet = scanner.nextLine();
                        System.out.println("The request was sent.");
                        outputStream.writeUTF("GET " + fileToGet);

                        String[] getResponse = inputStream.readUTF().split(" ");
                        if (getResponse[0].equals("404")) {
                            System.out.println("The response says that the file was not found!");
                        } else if (getResponse[0].equals("200")) {
                            String fileContent = String.join(" ", Arrays.copyOfRange(getResponse, 1, getResponse.length));
                            System.out.println("The content of the file is: " + fileContent);
                        }
                        break;

                    case "2": //PUT
                        System.out.print("Enter filename: ");
                        String fileToAdd = scanner.nextLine();
                        System.out.print("Enter file content: ");
                        String content = scanner.nextLine();
                        System.out.println("The request was sent.");
                        outputStream.writeUTF("PUT " + fileToAdd + " " + content);

                        String putResponse = inputStream.readUTF();
                        if (putResponse.equals("200")) {
                            System.out.println("The response says that the file was created!");
                        } else if (putResponse.equals("403")) {
                            System.out.println("The response says that creating the file was forbidden!");
                        }

                        break;

                    case "3": // DELETE
                        System.out.print("Enter filename: ");
                        String fileToDelete = scanner.nextLine();
                        System.out.println("The request was sent.");
                        outputStream.writeUTF("DELETE " + fileToDelete);

                        String deleteResponse = inputStream.readUTF();
                        if (deleteResponse.equals("200")) {
                            System.out.println("The response says that the file was successfully deleted!");
                        } else if (deleteResponse.equals("404")) {
                            System.out.println("The response says that the file was not found!");
                        }
                        break;

                    case "EXIT":
                        System.out.println("The request was sent.");
                        outputStream.writeUTF("EXIT 1");
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}