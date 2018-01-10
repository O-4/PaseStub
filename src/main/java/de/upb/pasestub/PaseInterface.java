package de.upb.pasestub;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Defines java client stub interface to connect to a pase server. 
 */
public interface PaseInterface {

    /**
     * Creates this interface through a http request to a pase server. 
     * @param constructor: The callable constructor that will be used in the http request to the pase server: <host>/<constructor>
     * @param parameters: Maps the names of the constructor parameters to their values. The JSON representation of this map will be used as the body of the http request.
     * @throws JsonProcessingException while JSON-serializing given parameters or JSON-deserializing the body of the response.
     * @throws IOException when there are problems connecting to the pase server.
     * @throws IllegalArgumentException if the returned JSON body of the server isn't valid. (Does'nt contain "class" and "id".) The server may return with error messages indicating that a parameter is missing or something. Thus client side arguments are 'illegal'.
     */
    public void create(String constructor, Map<String, Object> parameters)
            throws JsonProcessingException, IOException, IllegalArgumentException;

    /**
     * Retrieves the value of the attribute with the given name from the server.
     * @param attributeName: attribute name to be retrieved. Will be used in the http request to the pase server.
     * @return The JSON-deserialized object of the http response's body.
     * 
     * @throws JsonProcessingException while JSON-deserializing the body of the response.
     * @throws IOException when there are problems connecting to the pase server.
     * 
     */
    public Object getAttribute(String attributeName) throws IOException, JsonProcessingException;

    /**
     * Calls the function with the given functionName on the server and returns it's value.
     * @param functionName: function name to be called. Will be used in the http request to the pase server.
     * @param parameters: Maps the names of the function parameters to their values. The JSON representation of this map will be used as the body of the http request.
     * @return The JSON-deserialized object of the http response's body.
     * 
     * @throws JsonProcessingException while JSON-serializing given parameters or JSON-deserializing the body of the response.
     * @throws IOException when there are problems connecting to the pase server.
     * 
     */
    public Object callFunction(String functionName, Map<String, Object> parameters)
            throws JsonProcessingException, IOException;

    /**
     * Copies this object by making a 'copy' request to the Pase Server.
     * 
     * @return A new PaseInterface object with the same class name but different id.
     * 
     */
    public PaseInterface cloneObject() throws JsonProcessingException, IOException;
    

}