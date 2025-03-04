package org.httpServer.http;

import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage{

    private HttpMethod method;
    private String requestTarget;
    private String originalHTTPVersion;
    private HttpVersion bestCompatibleVersion;
    private HashMap<String,String> headers = new HashMap<>();

    HttpRequest(){}

    public String getOriginalHTTPVersion() {
        return originalHTTPVersion;
    }

    public HttpVersion getBestCompatibleVersion() {
        return bestCompatibleVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestTarget(){
        return requestTarget;
    }

    void setMethod(String methodName) throws HttpParsingException {
        for(HttpMethod method : HttpMethod.values()){
            if(methodName.equals(method.name())){
                this.method = method;
                return;
            }
        }
        throw new HttpParsingException(
            HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
        );
    }

    public void setRequestTarget(String requestTarget) throws HttpParsingException {
        if(requestTarget == null || requestTarget.isEmpty()){
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.requestTarget = requestTarget;
    }

    public void setHTTPVersion(String originalHTTPVersion) throws BadHttpParsingException, HttpParsingException {
        this.originalHTTPVersion = originalHTTPVersion;
        this.bestCompatibleVersion = HttpVersion.getBestCompatibleVersion(originalHTTPVersion);
        if(this.bestCompatibleVersion == null){
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    void addHeader(String headerName,String headerField){
        headers.put(headerName.toLowerCase(),headerField);
    }

    public Set<String> getHeadersName(){
        return headers.keySet();
    }

    public String getHeader(String headerName){
        return headers.get(headerName.toLowerCase());
    }
}
