package br.org.eldorado.hiaac;

import br.org.eldorado.hiaac.server.SocketServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Argument must be a number.");
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("PORT number needed as argument.");
            return;
        }

        try {
            new SocketServer(port).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
