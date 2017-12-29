package de.upb.pasestub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests that are executed if there is a Pase server running in the background.
 */
public class DeployTest {

	private static final double PRECISION = 0.01;

	/**
	 * Tests basic functionality.
	 */
	@Test
	public void deployTest1() throws Exception {
		PaseInstance instance = new PaseInstance("localhost:5000");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("a", 5);
		parameters.put("b", 20);
		boolean success = instance.create("plainlib.package1.b.B", parameters);

		Assert.assertTrue(success);

		// System.out.println(instance.getInstanceUrl());

		int a = (Integer) instance.getAttribute("a");
		Assert.assertEquals(a, 5);

		parameters = new HashMap<String, Object>();
		parameters.put("c", 2);
		int result = (Integer) instance.callFunction("calc", parameters);
        Assert.assertEquals(result, 45);
        
        PaseInstance instance2 = (PaseInstance) instance.cloneObject();
        Assert.assertEquals(instance.getClassName(), instance2.getClassName());
        Assert.assertNotEquals(instance.getId(), instance2.getId());

	}

	@Test
	public void deployTest_LinearRegression() throws Exception {
		PaseInstance instance = new PaseInstance("localhost:5000");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normalize", true);
		boolean success = instance.create("sklearn.linear_model.LinearRegression", parameters);

		Assert.assertTrue(success && instance.isCreated());

		parameters.clear();
		double[][] X = { { 0, 0 }, { 1, 1 }, { 2, 2 } };
		parameters.put("X", X);
		double[] y = { 0, 1, 2 };
		parameters.put("y", y);
		instance.callFunction("fit", parameters);

		// You will have to know the structure of the return value:
		ArrayList<Double> coef_ = (ArrayList<Double>) instance.getAttribute("coef_");
		Assert.assertEquals(0.5, (double) coef_.get(0), PRECISION);

		parameters.clear();
		double[][] X2 = { { 0.5, 1 }, { 1, 0.5 } };
		parameters.put("X", X2);
		ArrayList<Double> predictions = (ArrayList<Double>) instance.callFunction("predict", parameters);
		List<Double> expected = Arrays.asList(0.75, 0.75);
		Assert.assertEquals(expected.get(0), (double) predictions.get(0), PRECISION);

	}

/**
     * Tests this peace of python code:
     * >>> from sklearn import linear_model
     * >>> reg = linear_model.Ridge (alpha = .5)
     * >>> reg.fit ([[0, 0], [0, 0], [1, 1]], [0, .1, 1]) 
     * Ridge(alpha=0.5, copy_X=True, fit_intercept=True, max_iter=None,
     * normalize=False, random_state=None, solver='auto', tol=0.001)
     * >>> reg.predict([[1,2],[10,20],[100,200]])
     * array([   1.17272727,   10.5       ,  103.77272727])
     */
    @Test
    public void deployTest_Ridge() throws Exception {
        PaseInstance ridge = new PaseInstance();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("alpha", 0.5);
        ridge.create("sklearn.linear_model.Ridge", params);
        Assert.assertTrue(ridge.isCreated());

        params.clear();
        float[][] X_fit = {{0,0},{0,0},{1,1}};
        float[] y_fit = {0,0.1f,1};
        params.put("X", X_fit);
        params.put("y", y_fit);
        ridge.callFunction("fit", params);

        params.clear();
        float[][] X_predict = {{1,2},{10,20},{100,200}};
        params.put("X", X_predict);
        ArrayList<Double> predictionResults = (ArrayList<Double>) ridge.callFunction("predict", params);

        List<Double> expected = Arrays.asList(1.17272727, 10.5 , 103.77272727);
        for(int index = 0, size = predictionResults.size(); index<size; index++) {
            Assert.assertEquals(predictionResults.get(index), expected.get(index), 0.01);
        }
    }
}
