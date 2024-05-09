package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

class FileSystem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private volatile Hashtable<String, Integer> files;
    private final String ROOT;
    private volatile boolean exit = false;


    public FileSystem(String ROOT) {
        this.ROOT = ROOT;
        this.files = new Hashtable<>();
    }

    /**
     * Delete a file from the server.
     *
     * @param input  The input stream
     * @param output The output stream
     */
    protected void DELETE(DataInputStream input, DataOutputStream output) {
        try {
            int byNameOrId = input.readInt();

            String fileName = byNameOrId == 1 ? input.readUTF() : getByID(input.readInt());

            System.out.println(fileName);

            Path path = Paths.get(ROOT + "/" + fileName);

            if (path.toFile().exists()) {
                if (path.toFile().delete()) {
                    files.remove(fileName);
                    output.writeInt(200);
                } else {
                    output.writeInt(500);
                }
            } else {
                output.writeInt(404);
            }

        } catch (Exception e) {
            System.out.println("Error while deleting file: " + e.getMessage());
            e.printStackTrace();
            try {
                output.writeInt(500);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Get file data from file name or ID
     *
     * @param input  The input stream
     * @param output The output stream
     */
    protected void GET(DataInputStream input, DataOutputStream output) {
        try {
            int byNameOrId = input.readInt();

            String fileName = byNameOrId == 1 ? input.readUTF() : getByID(input.readInt());

            System.out.println(fileName);

            byte[] fileData = getFileData(fileName);

            if (fileData.length > 0) {
                output.writeInt(200);
                output.writeInt(fileData.length);
                output.write(fileData);
            } else {
                output.writeInt(404);
            }

        } catch (Exception e) {
            System.out.println("Error while getting file: " + e.getMessage());
            e.printStackTrace();
            try {
                output.writeInt(500);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Add a file to the server.
     *
     * @param input  The input stream
     * @param output The output stream
     * @return Status code: 200 if successful, 404 if not
     */
    protected void PUT(DataInputStream input, DataOutputStream output) {
        try {
            String fileName = input.readUTF();

            if (files.containsKey(fileName.strip())) {
                output.writeUTF("403");
            }

            int fileSize = input.readInt();
            byte[] fileData = new byte[fileSize];
            input.readFully(fileData, 0, fileSize);

            String id = saveFile(fileName.strip(), fileData);

            String message;
            if (id.matches("Error while saving file")) {
                message = "500";
            } else {
                message = "200 " + id;
            }
            output.writeUTF(message);
        } catch (Exception e) {
            System.out.println("Error while saving file: " + e.getMessage());
            e.printStackTrace();
            try {
                output.writeUTF("500");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Saves a file to the server storage
     *
     * @param fileName the name of the file
     * @param data     the data of the file
     * @return the ID of the file
     */
    private String saveFile(String fileName, byte[] data) {
        try {
            Path path = Paths.get(ROOT + "/" + fileName);
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                int id = generateId();
                fos.write(data, 0, data.length);
                files.put(fileName.strip(), id);
                return Integer.toString(id);
            }
        } catch (Exception e) {
            System.out.println("Error while saving file" + e.getMessage());
            return "Error while saving file";
        }
    }

    /**
     * Only used for testing purposes
     * Load all the files from the server storage and put them in a hashtable and generate an ID for each file
     */
    protected void loadFiles() {
        this.files = new Hashtable<>();
        try (Stream<Path> paths = Files.walk(Paths.get(ROOT))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        files.put(file.getFileName().toString(), generateId());
                    });
        } catch (Exception e) {
            System.out.println("Error while loading files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get file data from the server storage
     *
     * @param fileName the name of the file
     * @return the data of the file
     */
    private byte[] getFileData(String fileName) {
        try {
            return Files.readAllBytes(Paths.get(ROOT + "/" + fileName));
        } catch (Exception e) {
            System.out.println("Error while getting file data: " + e.getMessage());
            e.printStackTrace();
            return new byte[]{};
        }
    }

    /**
     * Generate a random unique ID
     *
     * @return Unique ID
     */
    private int generateId() {
        int ID_MAX_LENGTH = 2;
        int code = (int) (Math.random() * Math.pow(10, ID_MAX_LENGTH));

        if (files.contains(String.valueOf(code))) {
            return generateId();
        }
        return code;
    }

    public boolean isExit() {
        return exit;
    }

    public synchronized void setExit(boolean exit) {
        this.exit = exit;
    }

    private String getByID(int id) {
        AtomicReference<String> fileName = new AtomicReference<>("");

        files.forEach((key, value) -> {
            if (value.equals(id)) {
                fileName.set(key);
            }
        });

        return Objects.equals("", fileName.get()) ? "404" : fileName.get();
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        exit = false;
    }
}