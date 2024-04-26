package server;

import java.util.HashSet;
import java.util.Set;


public class StorageEmulator {

    private final Set<String> files;

    public StorageEmulator() {
        this.files = new HashSet<>();
    }

    public void addFile(String filename) {

        if (files.contains(filename) || !filename.matches("file([1-9]|10)") || files.size() >= 10) {
            System.out.println("Cannot add the file " + filename);
        } else {
            files.add(filename);
            System.out.println("The file " + filename + " added successfully");
        }
    }

    public void getFile(String filename) {
        if (files.contains(filename)) {
            System.out.println("The file " + filename + " was sent");
        } else {
            System.out.println("The file " + filename + " not found");
        }
    }

    public void deleteFile(String filename) {
        if (files.contains(filename)) {
            files.remove(filename);
            System.out.println("The file " + filename + " was deleted");
        } else {
            System.out.println("The file " + filename + " not found");
        }
    }

}

