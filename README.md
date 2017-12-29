# PaseStub

Java client stub for a [PASE](https://github.com/aminfa/Pase) server.

## Code Example

Take a look at the code example section of the [PASE repository](https://github.com/aminfa/Pase).
The same operations can be executed using a `PaseInstance`:

```java
    PaseInstance instance = new PaseInstance("localhost:5000"); // specify host

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("normalize", true);
    instance.create("sklearn.linear_model.LinearRegression", parameters);

    parameters.clear();
    double[][] X = {{0,0}, {1,1}, {2,2}};
    parameters.put("X", X);
    double [] y =  {0,1,2};
    parameters.put("y", y);
    instance.callFunction("fit", parameters);

    // You will have to know the structure of the return value:
    Map<String, Object> returnedMap = (Map<String,Object>) instance.getAttribute("coef_"); 
    ArrayList<Double> coef_ = (ArrayList<Double>) returnedMap.get("values");
    System.out.println("coef_: " + coef_); // coef_: [0.5,  0.5] 

    parameters.clear();
    double[][] X2 = {{0.5, 1}, {1, 0.5}};
    parameters.put("X", X2);
    Map<String, Object> returnedMap2 = (Map<String,Object>) instance.callFunction("predict", parameters); 
    ArrayList<Double> predictions = (ArrayList<Double>) returnedMap2.get("values");
    System.out.println("predictions: " + predictions); // predictions: [0.75,  0.75] 
```

## Installation

Clone this repository. Use the `makefile` in the `build/` folder to build this project:

```bash
cd build/
make
``` 

`pasestub.jar` will afterwards lie in the root folder and the `resources` folder will contain all the dependecies needed to use this stub.
You can alternatively then use maven to build this project too.