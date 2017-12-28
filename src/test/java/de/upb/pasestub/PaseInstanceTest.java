package de.upb.pasestub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import  com.github.tomakehurst.wiremock.junit.WireMockRule;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Rule;
import org.junit.Test;

public class PaseInstanceTest{

    public PaseInstanceTest(){
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(30000); // No-args constructor defaults to port 8080
    

    @Test
    public void test1() throws Exception{
        PaseInstance instance = new PaseInstance("localhost:5000");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("a", 5);
        parameters.put("b", 20);
        System.out.println(instance.create("plainlib.package1.b.B", parameters));
    }
    //@Test
    public void exampleTest() throws IOException{
        stubFor(post(urlEqualTo("/plainlib.package1.b.B"))
                .withHeader("content-type", equalToIgnoreCase("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson("{\"a\" : 10, \"b\" : 20}"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"7B495ECC9C\", \"class\": \"plainlib.package1.b.B\"}")));
    
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"a\" : 10, \"b\" : 20}");
        Request request = new Request.Builder()
            .url("http://localhost:30000/plainlib.package1.b.B")
            .post(body)
            .addHeader("content-type", "application/json")
            .build();
        Response response = client.newCall(request).execute();
        System.out.println("\n\n" + response.body().string().toString());
        
    }
}
