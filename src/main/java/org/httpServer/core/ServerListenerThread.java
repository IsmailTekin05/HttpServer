package org.httpServer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private int port;
    private String webroot;
    ServerSocket serverSocket;

    public ServerListenerThread(int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
                while(serverSocket.isBound() && !serverSocket.isClosed()){
                Socket socket = serverSocket.accept();

                LOGGER.info("Connection is accepted: " + socket.getInetAddress());

                HttpWorkerConnectionThread httpWorkerConnectionThread = new HttpWorkerConnectionThread(socket);
                httpWorkerConnectionThread.start();

            }
        }
        catch (Exception e){
            LOGGER.error("A problem with setting socket has occurred" + e);
        }finally{
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
    }
}
