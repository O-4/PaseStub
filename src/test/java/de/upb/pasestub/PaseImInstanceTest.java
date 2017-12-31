package de.upb.pasestub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import  com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class PaseImInstanceTest{

    private int port = 30000;
    private String host = "localhost:" + port;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);
    

    @Test
    public void testCorrect() throws IOException{
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

        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", 10);
        map.put("b", 20);
        PaseInterface instance = PaseImInstance.newInstance(host, constructor, map);

        int b = (Integer) instance.getAttribute("b");
        Assert.assertEquals(10, b);

        map = new HashMap<String, Object>();
        map.put("c", 5);
        int result = (Integer) instance.callFunction("calc", map);
        Assert.assertEquals(110, result);
    }
    
}