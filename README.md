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
    ArrayList<Double> coef_ = (ArrayList<Double>) instance.getAttribute("coef_");
    System.out.println("coef_: " + coef_); // coef_: [0.5,  0.5] 

    parameters.clear();
    double[][] X2 = {{0.5, 1}, {1, 0.5}};
    parameters.put("X", X2);
    ArrayList<Double> predictions = (ArrayList<Double>) instance.callFunction("predict", parameters);
    System.out.println("predictions: " + predictions); // predictions: [0.75,  0.75] 
```

For more code examples look at `DeployTest.java` in the `src/test` folder.

## Installation
Clone this repository.
You can use maven to build this project.

Alternatively, use the `makefile` in the `build/` folder to build this project:

```bash
make
``` 

`pasestub.jar` will afterwards lie in the build folder and the `lib` folder will contain all the dependencies needed to use this stub.