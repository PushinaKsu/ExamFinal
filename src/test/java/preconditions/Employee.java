package preconditions;

import apiUtils.AuthenticationUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class Employee {

    public static JSONObject buildEmployee(int companyId) {
        JSONObject json = new JSONObject();
        json.put("firstName", "Павел");
        json.put("lastName", "Дуров");
        json.put("middleName", "Валерьевич ");
        json.put("companyId", companyId);
        json.put("email", "DurovPV@example.com");
        json.put("url", "vk.com/id1");
        json.put("phone", "+78005553535");
        json.put("birthdate", "1999-05-06");
        json.put("isActive", true);
        return json;
    }

    public static Response createEmployee(int companyId) {
        return createEmployee(buildEmployee(companyId));
    }

    public static Response createEmployee(JSONObject body) {
        return RestAssured.given()
                .baseUri("https://x-clients-be.onrender.com")
                .header("x-client-token", AuthenticationUtils.getToken())
                .contentType("application/json")
                .body(body.toString())
                .post("/employee");
    }

    public static void checkFieldTypes(Map<String, Object> employee) {
        assertInstanceOf(Integer.class, employee.get("id"));
        assertInstanceOf(String.class, employee.get("firstName"));
        assertInstanceOf(String.class, employee.get("lastName"));
        assertInstanceOf(String.class, employee.get("middleName"));
        assertInstanceOf(Integer.class, employee.get("companyId"));
        assertInstanceOf(String.class, employee.get("email"));
        assertInstanceOf(String.class, employee.get("url"));
        assertInstanceOf(String.class, employee.get("phone"));
        assertInstanceOf(String.class, employee.get("birthdate"));
        assertInstanceOf(Boolean.class, employee.get("isActive"));
    }

    public static void checkRequiredFields(Map<String, Object> employee) {
        assertNotNull(employee.get("id"));
        assertNotNull(employee.get("firstName"));
        assertNotNull(employee.get("lastName"));
        assertNotNull(employee.get("middleName"));
        assertNotNull(employee.get("companyId"));
        assertNotNull(employee.get("email"));
        assertNotNull(employee.get("url"));
        assertNotNull(employee.get("phone"));
        assertNotNull(employee.get("birthdate"));
    }

    public static void checkKeys(Map<String, Object> employee) {
        Set<String> expectedKeys = Set.of(
                "id", "firstName", "lastName", "middleName",
                "companyId", "email", "url", "phone",
                "birthdate", "isActive"
        );

        Set<String> actualKeys = employee.keySet();
        assertEquals(expectedKeys, actualKeys);
    }
}
