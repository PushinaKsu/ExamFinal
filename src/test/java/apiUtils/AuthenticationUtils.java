package apiUtils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static utils.ConfigReader.get;

public class AuthenticationUtils {
    private static String token;

    public static String getToken() {
        if (token == null) {
            authenticate();
        }
        return token;
    }

    private static void authenticate() {
        String username = get("login");
        String password = get("passwordApi");
        String apiUrl = get("apiUrl");

        Response response = RestAssured.given()
                .baseUri(apiUrl)
                .contentType("application/json")
                .body("{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }")
                .post("/auth/login");

        token = response.jsonPath().getString("userToken");

    }
}
