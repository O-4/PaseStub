package de.upb.pasestub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class PaseInstance{

    private String host;
    private String className;
    private String id;

    public PaseInstance(String host){
        this.host = host;
    }

    public boolean create(String constructor, Map<String, Object> parameters) throws JsonProcessingException, IOException{
        String jsonString = serialize(parameters);
        Response serverResponse = requestCall(constructor, jsonString);
        if(serverResponse.code() != 200){
            return false;
        }
        Map<String, Object> returnValues = deserializeMap(serverResponse.body().string());
        if(returnValues.containsKey("id") && returnValues.containsKey("class")){
            id = returnValues.get("id").toString();
            className = returnValues.get("class").toString();
            return true;
        }
        else{
            return false;
        }
    }

    public boolean call(String function, Map<String, Object> parameters) throws JsonProcessingException, IOException{
        // TODO
        return false;
    }

    

    Response requestCall(String callable, String bodyString) throws IOException{
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, bodyString);
        Request request = new Request.Builder()
            .url("http://" + host + "/" + callable)
            .post(body)
            .addHeader("content-type", "application/json")
            .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    String serialize(Map<String, Object> map) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(map);
        return jsonResult;
    }

    Map<String, Object> deserializeMap(String jsonString) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef 
            = new TypeReference<HashMap<String, Object>>() {};
        Map<String, Object> map = mapper.readValue(jsonString, typeRef);
        return map;
    }
    Object deserializeObject(String jsonString) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Object> typeRef 
            = new TypeReference<Object>() {};
        Object pojo = mapper.readValue(jsonString, typeRef);
        return pojo;
    }

}