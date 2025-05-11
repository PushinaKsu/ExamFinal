package preconditions;

import apiUtils.AuthenticationUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class Company {
    public static int createCompany() {
        JSONObject body = new JSONObject();
        body.put("name", "Test Company");
        body.put("description", "Some description");

        Response response = RestAssured.given()
                .baseUri("https://x-clients-be.onrender.com")
                .header("x-client-token", AuthenticationUtils.getToken())
                .contentType("application/json")
                .body(body.toString())
                .post("/company");

        return response.jsonPath().getInt("id");
    }
}