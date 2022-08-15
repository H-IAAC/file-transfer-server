package br.org.eldorado.hiaac.server;

import br.org.eldorado.hiaac.Actions;
import br.org.eldorado.hiaac.utils.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private static final String TAG = "SocketServer";
    private final int PORT;
    private ServerSocket serverSocket;
    private int connectionCounter;
    private boolean isServerStarted = false;

    public SocketServer(int port) {
        this.PORT = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        isServerStarted = true;
        Logger.print(TAG, "Listening for connections on port " + PORT + " ....");

        while (isServerStarted) {
            new ClientHandler(serverSocket.accept(), connectionCounter++).start();
            if(connectionCounter > 9999) connectionCounter = 0;
        }
    }

    public void stop() throws IOException {
        isServerStarted = false;
        serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private int connectionNumber;

        public ClientHandler(Socket clientSocket, int connectionNumber) {
            this.clientSocket = clientSocket;
            this.connectionNumber = connectionNumber;
            Logger.print(TAG, "New connection was established", connectionNumber);
        }

        @Override
        public void run() {
            try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream())) {
                int action = dataInputStream.readInt();
                switch (action) {
                    case Actions
                            .SEND_MESSAGE:
                        handleSendMessage(clientSocket);
                        break;
                    case Actions.SEND_FILE:
                        handleSendFile(dataInputStream);
                        break;
                    default:
                        Logger.print(TAG, "Unexpected action value.");
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Logger.print(TAG, "Connection finished", connectionNumber);
            }
        }

        private void handleSendMessage(Socket clientSocket) throws IOException {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine = in.readLine();
                Logger.print(TAG, "Message received: " + inputLine, connectionNumber);
            }
        }

        private void handleSendFile(DataInputStream dataInputStream) throws IOException {
            int fileNameLength = dataInputStream.readInt();
            if (fileNameLength > 0) {
                byte[] fileNameBytes = new byte[fileNameLength];
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                String fileName = new String(fileNameBytes);
                int fileContentLength = dataInputStream.readInt();
                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                    File fileToDownload = new File(fileName);
                    String filePath = fileToDownload.getAbsolutePath();
                    String parentPath = filePath
                            .substring(0, filePath.length() - fileToDownload.getName().length());

                    File fileNameDir = new File(parentPath);
                    if (!fileNameDir.exists()){
                        fileNameDir.mkdirs();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    fileOutputStream.write(fileContentBytes);
                    fileOutputStream.close();

                    Logger.print(TAG, "Downloaded file: " + fileName, connectionNumber);
                }
            }
        }
    }
}
