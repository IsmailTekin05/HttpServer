package org.httpServer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpWorkerConnectionThread extends Thread{

    private Socket socket;
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpWorkerConnectionThread.class);


    HttpWorkerConnectionThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try{
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            String html = " <html><head><title>Simple HTTP Server</title></head><body><h1>Hi</h1></body></html>";

            final String CRLF = "\n\r"; //13, 10 (ASCII)

            String response = "HTTP/1.1 200 OK" + CRLF + //Status line: Http_version response_code response_message
                    "Content-Length" + html.getBytes().length + CRLF
                    + CRLF
                    + html
                    + CRLF + CRLF;// HEADER

            outputStream.write(response.getBytes());

            LOGGER.info("Connection Processing is Finished..");
        }catch (IOException e){
            LOGGER.error("Problem with communication has occurred" + e);
        }finally{
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }
}
