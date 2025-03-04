package org.httpServer.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20; // 32
    private static final int CR = 0x0D; // 13
    private static final int LF = 0x0A; // 10


    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);

        HttpRequest request = new HttpRequest();

        try {
            parseRequestLine(inputStreamReader,request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            parseHeaders(inputStreamReader,request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        parseBody(inputStreamReader,request);

        return request;
    }

    private void parseRequestLine(InputStreamReader inputStreamReader, HttpRequest request) throws IOException, HttpParsingException {
        int _byte;

        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        StringBuilder processingDataBuffer = new StringBuilder();

        while( (_byte = inputStreamReader.read()) >= 0){
            if(_byte == CR){
                _byte = inputStreamReader.read();
                if(_byte == LF){
                    LOGGER.debug("Request Line VERSION to Process: {}",processingDataBuffer);
                    if(!methodParsed || !requestTargetParsed){
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    try {
                        request.setHTTPVersion(processingDataBuffer.toString());
                    } catch (BadHttpParsingException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    return;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            if(_byte == SP){
                if(!methodParsed){
                    LOGGER.debug("Request Line METHOD to Process: {}",processingDataBuffer);
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                }else if(!requestTargetParsed){
                    LOGGER.debug("Request Line REQUEST TARGET to Process: {}",processingDataBuffer);
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0,processingDataBuffer.length());
            } else{
                processingDataBuffer.append((char) _byte);
                if(!methodParsed){
                    if(processingDataBuffer.length() > HttpMethod.MAX_LENGTH){
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    private void parseHeaders(InputStreamReader inputStreamReader, HttpRequest request) throws HttpParsingException, IOException {
        int _byte;
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean crlfFound = false;

        while( (_byte = inputStreamReader.read()) >= 0) {
            if (_byte == CR) {
                _byte = inputStreamReader.read();
                if (_byte == LF) {
                    if(!crlfFound){
                        crlfFound = true;
                        processSingleHeaderFiled(processingDataBuffer,request);
                        processingDataBuffer.delete(0,processingDataBuffer.length());
                    }

                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }else{
                crlfFound = true;
                processingDataBuffer.append((char) _byte);
            }
        }
    }

    private void processSingleHeaderFiled(StringBuilder processingDataBuffer, HttpRequest request) throws HttpParsingException {

        String rawHeaderField = processingDataBuffer.toString();
        Pattern pattern = Pattern.compile("^(?<fieldName>[!#%&'*+\\-./^_|\\dA-Za-z]+):\\s*(?<fieldValue>.+)$");


        Matcher matcher = pattern.matcher(rawHeaderField);
        if(matcher.matches()){
            String fieldName = matcher.group("fieldName");
            String fieldValue = matcher.group("fieldValue");
            request.addHeader(fieldName,fieldValue);
        }else{
         throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    private void parseBody(InputStreamReader inputStreamReader, HttpRequest request) {

    }


}
