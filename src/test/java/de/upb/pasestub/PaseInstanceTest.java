package de.upb.pasestub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import  com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class PaseInstanceTest{

    public PaseInstanceTest(){
    }
    private int port = 30000;
    private String host = "localhost:" + port;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);
    

    @Test
    public void testCorrectCreate() throws IOException{
        // Create mock
        String constructor = "plainlib.package1.b.B";
        stubFor(post(urlEqualTo("/" + constructor))
                .withHeader("content-type", equalToIgnoreCase("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson("{\"a\" : 10, \"b\" : 20}"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"7B495ECC9C\", \"class\": \"plainlib.package1.b.B\"}")));
        
        PaseInstance instance = new PaseInstance(host);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", 10);
        map.put("b", 20);

        Assert.assertFalse(instance.isCreated());
        boolean success = instance.create(constructor, map);
        Assert.assertTrue(success);
        Assert.assertTrue(instance.isCreated());

        Assert.assertEquals("7B495ECC9C", instance.getId());
        Assert.assertEquals("plainlib.package1.b.B", instance.getClassName());

        Assert.assertEquals(host + "/plainlib.package1.b.B/7B495ECC9C", instance.getInstanceUrl());

    }
    @Test
    public void testCorrect1() throws IOException{
        // Create mock
        String constructor = "plainlib.package1.b.B";
        stubFor(post(urlEqualTo("/" + constructor))
                .withRequestBody(equalToJson("{\"a\" : 10, \"b\" : 20}"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"7B495ECC9C\", \"class\": \"plainlib.package1.b.B\"}")));

        stubFor(get(urlEqualTo("/plainlib.package1.b.B/7B495ECC9C/b")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("10")));
        
        stubFor(post(urlEqualTo("/plainlib.package1.b.B/7B495ECC9C/calc"))
            .withRequestBody(equalToJson("{\"c\" : 5}"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("110")));

        
        PaseInstance instance = new PaseInstance(host);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", 10);
        map.put("b", 20);
        instance.create(constructor, map);

        int b = (Integer) instance.getAttribute("b");
        Assert.assertEquals(10, b);

        map = new HashMap<String, Object>();
        map.put("c", 5);
        int result = (Integer) instance.callFunction("calc", map);
        Assert.assertEquals(110, result);
    }
    
    @Test(expected = IOException.class)
    public void testWrongHostCreate() throws IOException{

        PaseInstance instance = new PaseInstance("localhost:10000"); // server shouldn't be accessible
        instance.create("con", new HashMap<String, Object>()); // Should be throwing IOException
    }

    @Test(expected = IllegalStateException.class)
    public void testNoCreateCall() throws IOException{
        PaseInstance instance = new PaseInstance("localhost:10000"); // server shouldn't be accessible
        instance.callFunction("func",  new HashMap<String, Object>());
    }
    
    @Test
    public void cloneTest() throws Exception{

        stubFor(post(urlEqualTo("/construct"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"7B495ECC9C\", \"class\": \"class_name\"}")));

        stubFor(get(urlEqualTo("/class_name/copy/7B495ECC9C"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"7B495ECC99\", \"class\": \"class_name\"}")));

        PaseInstance instance = new PaseInstance(host);
        instance.create("construct", new HashMap<>());
        PaseInstance instanceClone = (PaseInstance) instance.cloneObject();
        
        Assert.assertEquals(instance.getClassName(), instanceClone.getClassName());
        Assert.assertNotEquals(instance.getId(), instanceClone.getId());
    }
}
