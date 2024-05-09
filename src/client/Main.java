package client;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            new Client();
        } catch (IOException e) {
            //System.out.println("Unknown status!");
        }
    }
}
