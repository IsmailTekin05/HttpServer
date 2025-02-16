package org.httpServer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class Json{

    private static final ObjectMapper objectMapper = defaultObjectMapper();

    public static ObjectMapper defaultObjectMapper(){
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return om;
    }

    public static JsonNode parse(String jsonSrc) throws IOException {
        return objectMapper.readTree(jsonSrc);
    }

    public static <T> T fromJson(JsonNode node, Class<T> clazz) throws JsonProcessingException {
            return objectMapper.treeToValue(node,clazz);
    }

    public static JsonNode toJson(Object obj){
        return objectMapper.valueToTree(obj);
    }

    public static String stringify(JsonNode json) throws JsonProcessingException {
        return generateJson(json,false);
    }

    public static String stringifyPretty(JsonNode json) throws JsonProcessingException {
        return generateJson(json,true);
    }

    public static String generateJson(Object obj,boolean pretty) throws JsonProcessingException {
        ObjectWriter writer = objectMapper.writer();
        if(pretty){
            writer = writer.with(SerializationFeature.INDENT_OUTPUT);
        }
        return writer.writeValueAsString(obj);
    }
}
