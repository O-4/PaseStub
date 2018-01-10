package de.upb.pasestub;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Map;

/**
 * Immutable PaseInterface Implementation.
 * This implementation wraps 'PaseInstance'  but hides the 'create' function in a static factory method. Thus after construction, a PaseImInstance object is always 'created'.
 */
public final class PaseImInstance implements PaseInterface {

    /**
     * Uses the implementation of PaseInstance. This field should always be assigned with a PaseInstance object with the 'created'-Flag = true. 
     */
    private final PaseInstance mutableInstance;

    /**
     * Wraps the createdInstance if it is 'created'.
     */
    public PaseImInstance(PaseInstance createdInstance){
        if(createdInstance == null){
            throw new NullPointerException();
        }
        else if(createdInstance.isCreated()){
            mutableInstance = createdInstance;
        }
        else {
            throw createdInstance.createNotCalled();
        }
    }

    // Static factory methods:
    public static final PaseImInstance newInstance(String host, String constructor, Map<String, Object> parameters) throws IOException, JsonProcessingException, IllegalArgumentException {
        PaseInstance paseInstance = new PaseInstance(host);
        // Make http create call.
        paseInstance.create(constructor, parameters);
        // Return immutable.
        return new PaseImInstance(paseInstance);
    }

    /**
     * Overloads the newInstance method with host = 'localhost:5000'
     */
    public static final PaseImInstance newInstance(String constructor, Map<String, Object> parameters) throws IOException, JsonProcessingException, IllegalArgumentException {
        return PaseImInstance.newInstance("localhost:5000", constructor, parameters);
    }


    // Interface methods:
	@Override
	public void create(String constructor, Map<String, Object> parameters)
			throws JsonProcessingException, IOException {
        // Does nothing. Already created by definition.
		return;
	}

	@Override
	public Object getAttribute(String attributeName) throws IOException, JsonProcessingException {
        // Delegate function call to inner PaseInstace.
        return mutableInstance.getAttribute(attributeName);
	}

	@Override
	public Object callFunction(String functionName, Map<String, Object> parameters)
			throws JsonProcessingException, IOException {
        // Delegate function call to inner PaseInstace.
        return mutableInstance.callFunction(functionName, parameters);
	}

	@Override
	public PaseInterface cloneObject() throws JsonProcessingException, IOException {
        // Clone the inner instance and wrap it in another PaseImInstance.
		return new PaseImInstance((PaseInstance)mutableInstance.cloneObject());
	}
}