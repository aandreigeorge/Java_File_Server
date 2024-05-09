package server;

import java.io.*;
import java.nio.file.Paths;

class SerializingTools {

    static void serialize(FileSystem fileSystem, String path) {

        try {
            FileOutputStream fos = new FileOutputStream(Paths.get(path).toFile());
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(fileSystem);
            oos.close();
        } catch (IOException e) {
            System.out.println("Error while serializing file!");
        }
    }

    static FileSystem deserialize(String path) {

        try {
            FileInputStream fis = new FileInputStream(Paths.get(path).toFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);
            FileSystem fileSystem = (FileSystem) ois.readObject();
            ois.close();
            return fileSystem;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while deserializing file!");
            return null;
        }
    }

}
