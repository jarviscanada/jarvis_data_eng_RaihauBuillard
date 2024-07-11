package ca.jrvs.apps.jdbc.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JsonParser {

    public static String toJson(Object object, boolean prettyJson, boolean includeNullValues) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if(!includeNullValues){
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

        if(prettyJson){
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return mapper.writeValueAsString(object);
    }

    public static <T> T toObjectFromJson(String json, Class clazz) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return (T) mapper.readValue(json, clazz);
    }

    public static <T> T toSpecificObjectFromJson(String json, String nestedValue, Class clazz) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        JsonNode detailsNode = node.get(nestedValue);
        return (T) mapper.treeToValue(detailsNode, clazz);
    }

}
