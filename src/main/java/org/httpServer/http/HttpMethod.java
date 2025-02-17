package org.httpServer.http;

public enum HttpMethod {
    GET,HEAD;

    public static final int MAX_LENGTH;

    static{
        int tempMaxLength = -1;
        for(HttpMethod method : HttpMethod.values()){
            int len = method.name().length();
            if(len > tempMaxLength){
                tempMaxLength = len;
            }
        }
        MAX_LENGTH = tempMaxLength;
    }

}
