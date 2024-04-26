package server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        StorageEmulator storage = new StorageEmulator();
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {

            command = scanner.nextLine().trim();
            String[] parts = command.split("\\s+");

            if (parts[0].equalsIgnoreCase("exit")) {
                break;
            } else if (parts.length != 2) {
                System.out.println("Invalid command. Usage: action filename");
                continue;
            }

            String action = parts[0];
            String filename = parts[1];

            switch (action) {
                case "add":
                    storage.addFile(filename);
                    break;
                case "get":
                    storage.getFile(filename);
                    break;
                case "delete":
                    storage.deleteFile(filename);
                    break;
                default:
                    System.out.println("Invalid action. Use 'add', 'get', 'delete', or 'exit'.");
            }
        }
    }
}