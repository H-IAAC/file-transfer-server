package br.org.eldorado.hiaac.client;

import br.org.eldorado.hiaac.Actions;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private final String IP;
    private final int PORT;

    public SocketClient(String IP, int PORT) {
        this.IP = IP;
        this.PORT = PORT;
    }

    public void sendMessage(String msg) throws IOException {
        try (Socket socket = new Socket(IP, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.writeInt(Actions.SEND_MESSAGE);
            out.println(msg);
            dataOutputStream.flush();
        }
    }

    public void sendFile(File file) throws IOException {
        sendFileToDir(file, "");
    }

    public void sendFileToDir(File file, String dir) throws IOException {
        try (Socket socket = new Socket(IP, PORT);
             FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            String fileName = file.getName();
            fileName = dir + fileName;
            byte[] fileNameBytes = fileName.getBytes();
            byte[] fileBytes = new byte[(int) file.length()];

            fileInputStream.read(fileBytes);
            dataOutputStream.writeInt(Actions.SEND_FILE);
            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);
            dataOutputStream.writeInt(fileBytes.length);
            dataOutputStream.write(fileBytes);
            dataOutputStream.flush();
        }
    }
}
