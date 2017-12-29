package de.upb.pasestub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Mutable PaseInterface Implementation.
 */
public final class PaseInstance implements PaseInterface {
    /**
     * Flag that indicated that the create function has beed called successfully.
     */
    private boolean creationFlag = false;

    /**
     * Contains host url (without http://)
     */
    private String host;
    /**
     * Class Name as returned by the Pase Server.
     */
    private String className;
    /**
     * Id of this instance as returned by the Pase Server.
     */
    private String id;

    /**
     * PaseInstance defined with the given http host. (Don't include http:// in host)
     */
    public PaseInstance(String host) {
        this.host = host;
    }

    /**
     * PaseInstance defined to access the Pase with standard port running on the same machine.
     */
    public PaseInstance() {
        this("localhost:5000");
    }

    /**
     * Constructor is used by 'copy' method to initialize a 'created' PaseInstance object. 
     */
    private PaseInstance(String host, String className, String id){
        this(host);
        this.className = className;
        this.id = id;
        this.creationFlag = true;
    }

    // GETTERS:

    /**
     * Returns the pase instance id.
     */
    public String getId() {
        checkCreated(); // may throw Exception.
        return id;
    }

    /**
     * Returns class Name that was assigned 
     */
    public String getClassName() {
        checkCreated(); // may throw Exception.
        return className;
    }

    /**
     * Returns the host url that this object was assigned to use.
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the instance url that is used to access this instance on the pase server.
     */
    String getInstanceUrl() {
        checkCreated(); // may throw Exception.
        return getHost() + "/" + getClassName() + "/" + getId();
    }

    // INTERFACE: 
    @Override
    public boolean create(String constructor, Map<String, Object> parameters)
            throws JsonProcessingException, IOException {
        if (isCreated()) {
            // create was already called. Stop create
            throw createAlreadyCalled();
        }
        if (constructor == null || constructor.trim().isEmpty() || parameters == null) {
            throw new NullPointerException();
        }
        String jsonString = serialize(parameters);
        Response serverResponse = httpPost(host + "/" + constructor, jsonString);
        if (serverResponse.code() != 200) {
            return false;
        }
        Map<String, Object> returnValues = deserializeMap(serverResponse.body().string());
        if (returnValues.containsKey("id") && returnValues.containsKey("class")) {
            id = returnValues.get("id").toString();
            className = returnValues.get("class").toString();
            creationFlag = true;
            return true;
        } else {
            return false;
        }
    }
    

    @Override
    public Object getAttribute(String attributeName) throws IOException, JsonProcessingException {
        checkCreated();
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new NullPointerException();
        }
        Response serverResponse = httpGet(getInstanceUrl() + "/" + attributeName);
        if (serverResponse.code() != 200) {
            throw responseErrorCode(serverResponse);
        }
        Object pojo = deserializeObject(serverResponse.body().string());
        return pojo;
    }

    @Override
    public Object callFunction(String functionName, Map<String, Object> parameters)
            throws JsonProcessingException, IOException {
        checkCreated();
        if (functionName == null || functionName.trim().isEmpty() || parameters == null) {
            throw new NullPointerException();
        }
        String jsonString = serialize(parameters);
        Response serverResponse = httpPost(getInstanceUrl() + "/" + functionName, jsonString);
        if (serverResponse.code() != 200) {
            throw responseErrorCode(serverResponse);
        }
        Object pojo = deserializeObject(serverResponse.body().string());
        return pojo;
    }

	@Override
	public PaseInterface cloneObject() throws JsonProcessingException, IOException {
        checkCreated();
        Response serverResponse = httpGet(host +  "/" + getClassName() +  "/copy/" + getId());
        if (serverResponse.code() != 200) {
            throw responseErrorCode(serverResponse);
        }
        Map<String, Object> returnMap = deserializeMap(serverResponse.body().string());
        String newClassName = returnMap.get("class").toString();
        String newId = returnMap.get("id").toString();
        return new PaseInstance(host, newClassName, newId);
	}

    // HELPER FUNCTIONS:
    /**
     * Handles basic http post using OkHttp.
     */
    private Response httpPost(String url, String bodyString) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, bodyString);
        Request request = new Request.Builder().url("http://" + url).post(body)
                .addHeader("content-type", "application/json").build();
        Response response = client.newCall(request).execute();
        return response;
    }

    /**
     * Handles basic http get using OkHttp.
     */
    private Response httpGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://" + url).addHeader("content-type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }

    //TODO: custom json parser if there are any problems parsing objects.
    /**
     * JSON-Serializes the given map. 
     */
    private String serialize(Map<String, Object> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        return jsonResult;
    }

    /**
     * JSON-Deserializes the given json string to a map.
     */
    private Map<String, Object> deserializeMap(String jsonString) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        Map<String, Object> map = mapper.readValue(jsonString, typeRef);
        return map;
    }

    /**
     * JSON-Deserializes the given json string to a pojo.
     */
    private Object deserializeObject(String jsonString) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Object> typeRef = new TypeReference<Object>() {
        };
        Object pojo = mapper.readValue(jsonString, typeRef);
        return pojo;
    }

    /**
     * Returns true, if the create method has beed called successfully.
     */
    boolean isCreated() {
        return creationFlag;
    }

    /**
     * Throws RuntimeException if create hasn't been called before. 
     */
    private void checkCreated() {
        if (!isCreated()) {
            throw createNotCalled();
        }
    }

    // Exceptions:
    /**
     * Creates a IllegalArgumentException for Server responses that aren't 200.
     */
    private IllegalArgumentException responseErrorCode(Response serverResponse) throws IOException {
        return new IllegalArgumentException(serverResponse.code() + "\n: " + serverResponse.body().string());
    }

    /**
     * Creates a IllegalStateException that indicates that the create function has not been called before. 
     * Used in functions where create-state is mandatory.
     */
    private IllegalStateException createNotCalled() {
        return new IllegalStateException("create function was not called.");
    }

    /**
     * Creates a IllegalStateException that indicates that the create function has already been called. 
     * Used in the create function to avoid calling it twice.
     */
    private IllegalStateException createAlreadyCalled() {
        return new IllegalStateException("create function has already been called.");
    }

}