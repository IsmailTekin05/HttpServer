package org.httpServer.core;

import org.httpServer.core.io.WebRootHandler;
import org.httpServer.core.io.WebRootNotFoundException;
import org.httpServer.core.io.ReadFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpWorkerConnectionThread extends Thread {

    private Socket socket;
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpWorkerConnectionThread.class);
    private WebRootHandler webRootHandler;

    HttpWorkerConnectionThread(Socket socket, String webRootPath) throws WebRootNotFoundException {
        this.socket = socket;
        this.webRootHandler = new WebRootHandler(webRootPath);
    }

    @Override
    public void run() {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            // Example request target, you should parse the actual request to get this
            String requestTarget = "/index.html";

            String mimeType = webRootHandler.getFileMimeType(requestTarget);
            byte[] fileData = webRootHandler.getFileByteArrayData(requestTarget);

            final String CRLF = "\r\n"; // 13, 10 (ASCII)

            String response = "HTTP/1.1 200 OK" + CRLF + // Status line: Http_version response_code response_message
                    "Content-Type: " + mimeType + CRLF +
                    "Content-Length: " + fileData.length + CRLF +
                    CRLF;

            outputStream.write(response.getBytes());
            outputStream.write(fileData);
            outputStream.write(CRLF.getBytes());

            LOGGER.info("Connection Processing is Finished..");
        } catch (IOException | ReadFileException e) {
            LOGGER.error("Problem with communication has occurred: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}