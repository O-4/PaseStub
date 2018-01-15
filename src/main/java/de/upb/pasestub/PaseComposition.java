package de.upb.pasestub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Handles composition calls.
 */
public class PaseComposition {
    private final String composition;
    // list of variable names from the compostion text
    private final List<String> returnVariables; // these are returned from the server by their value
    private final List<String> storeVariables;  // these are returned from the server as a reference
    
    /**
     * Creates a PaseComposition Object from a json string.
     * @param composition: Composition as a json string.
     */
    public PaseComposition(String composition) throws IOException{
        this.composition = composition;

        // Parses the composition json string.
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
        Map<String, Object> map = mapper.readValue(composition, typeRef);
        // Extract the return variable list:
        if(map.containsKey("return")){
            Object obj = map.get("return");
            if(obj instanceof List){
                // This is the usecase
                returnVariables =  (List) obj;
            }
            else{
                // The "return" field could also be set to null. In this case ignore.
                returnVariables = new ArrayList<>(); // Empty list.
            }
        }
        else { // the composition has to have a return field.
            throw new RuntimeException("The composition doesn't have a return field defined:\n" + composition);
        }
        // extract store field.
        if(map.containsKey("store")){
            Object obj = map.get("store");
            if(obj instanceof List){
                // This is the usecase
                storeVariables =  (List) obj;
            }else{
                // it was set to null or likewise.
                storeVariables = new ArrayList<>(); // Empty list.
            }
        }
        else { 
            // List wasn't assigned because it was omitted in the json string:
            storeVariables = new ArrayList<>(); // Empty list.
        }
    }
    /**
     * Creates a PaseComposition object using a json file.
     */
    public static PaseComposition fromFilePath(String pathToFile) throws IOException{
        byte[] encoded = Files.readAllBytes(Paths.get(pathToFile));
        return new PaseComposition(new String(encoded, Charset.defaultCharset()));
    }

    /**
     * Executes the composition on server on the given url.
     */
    public Map<String, Object> execute(String host) throws IOException {
        // HTTP POST
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, composition);
        Request request = new Request.Builder().url("http://" + host + "/composition").post(body)
                .addHeader("content-type", "application/json").build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new RuntimeException("ERROR: Server returned code " + response.code() + "\n" + response.body().string());
        }
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
        Map<String, Object> map = mapper.readValue(response.body().string(), typeRef);
        Map<String, Object> returnMap = new HashMap<>();
        if(!map.containsKey("return")){ 
            // doesn't contain return. return an empty map:
            return returnMap;
        }else{
            map = (Map<String, Object>) map.get("return");
        }
        // Now extraxt values:
        for(String variable : returnVariables){
            returnMap.put(variable, map.get(variable));
        }
        for(String variable : storeVariables){
            if(map.get(variable) == null){
                continue;
            }
            Map<String, Object> idMap = (Map<String, Object>) map.get(variable);
            returnMap.put(variable, new PaseInstance(host, (String) idMap.get("class"),  (String) idMap.get("id")));

        }
        return returnMap;
    }

}