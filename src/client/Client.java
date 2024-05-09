package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;


class Client {

    private final Scanner scanner;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 34522;
    private static final String CLIENT_DATA_LOCATION = "C:\\Users\\andre\\Desktop\\Server File\\src\\client\\data";
    private DataInputStream inputStream;
    private DataOutputStream outputStream;


    Client() throws IOException {

        scanner = new Scanner(System.in);
        connectToServer();
        String userInput;

        do {
            System.out.print("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
            userInput = scanner.nextLine().toUpperCase();

            if (!userInput.isEmpty()) {
                switch (userInput) {
                    case "1" -> getFile();
                    case "2" -> saveFile();
                    case "3" -> deleteFile();
                    case "EXIT" -> {
                        outputStream.writeInt(0);
                        scanner.close();
                        System.exit(0);
                    }
                }
            }
        } while (true);

    }

    private void connectToServer() {

        try (
                Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            connectToServer(); //Trying again to connect
        }
    }

    private void getFile() {

        try {
            requestFile(1);
            int serverResponse = inputStream.readInt();

            switch (serverResponse) {
                case 200 -> {
                    int fileSize = inputStream.readInt();
                    byte[] fileData = new byte[fileSize];
                    inputStream.readFully(fileData, 0, fileSize);
                    System.out.print("The file was downloaded! Specify a name for it: ");
                    String fileName = scanner.nextLine();

                    try {
                        Path path = Paths.get(CLIENT_DATA_LOCATION + "\\" + fileName);
                        Files.write(path, fileData);
                        System.out.println("File saved on the hard drive!");
                    } catch (IOException e) {
                        System.out.println("Error saving file! " + e.getMessage());
                    }
                }

                case 405 -> System.out.println("The response says that this file is not found!");

                default -> System.out.println("Unknown server response!");
            }

        } catch (IOException e) {
            System.err.println("An error occurred while downloading the file. Please check your network connection and try again.\n");
        }
    }

    private void saveFile() {

        System.out.print("Enter name of the file: ");
        String fileFromClient = scanner.nextLine();
        File file = new File(CLIENT_DATA_LOCATION + "\\" + fileFromClient);

        if (file.exists()) {

            System.out.print("Enter the name of the file to be saved on server: ");
            String fileNameOnServer = scanner.nextLine();
            if (fileNameOnServer.isEmpty()) {
                fileNameOnServer = fileFromClient;
            }

            try {
                byte[] fileData = Files.readAllBytes(file.toPath());
                outputStream.writeInt(2);
                outputStream.writeUTF(fileNameOnServer);
                outputStream.writeInt(fileData.length);
                outputStream.write(fileData);
                System.out.println("The request was sent");

                String serverResponse = inputStream.readUTF();

                if (serverResponse.substring(0, 3).matches("200")) {
                    System.out.println("Response says that the file is saved! ID = " + Integer.valueOf(serverResponse.substring(4)));
                } else {
                    System.out.println("The response says that the file already exists!");
                }

            } catch (IOException e) {
                System.err.println("An error occurred while creating the file. Please check your network connection and try again.\n");
            }

        } else {
            System.out.println("The file does not exist!");
        }
    }

    private void deleteFile() {

        requestFile(3);
        try {
            int serverResponse = inputStream.readInt();
            switch (serverResponse) {
                case 200 -> System.out.println("The response says that this file was deleted successfully!");
                case 404 -> System.out.println("The response says that this file is not found!");
                default -> System.out.println("Unknown server response");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while deleting the file. Please check your network connection and try again.\n");
        }
    }

    private void requestFile(int getOrDelete) {

        String message = getOrDelete == 1 ? "get" : "delete";
        System.out.print("Do you want to " + message + " the file by name or by id (1 - name, 2 - id):");

        try {

            int byNameOrId = Integer.parseInt(scanner.nextLine());
            outputStream.writeInt(getOrDelete); // Sending GET or DELETE request to server


            if (byNameOrId == 1) {
                System.out.print("Enter filename: ");
                String fileName = scanner.nextLine();
                outputStream.writeInt(1); //sending by NAME request to server
                outputStream.writeUTF(fileName);

            } else if (byNameOrId == 2) {
                System.out.println("Enter id: ");
                int fileId = Integer.parseInt(scanner.nextLine());
                outputStream.writeInt(2); //sending by ID request to server
                outputStream.writeInt(fileId);

            } else {
                System.out.println("Invalid command!");
            }

        } catch (IOException | InputMismatchException e) {
            System.err.println("Invalid command, please enter a number!");
        }

        System.out.println("The request was sent");
    }
}
