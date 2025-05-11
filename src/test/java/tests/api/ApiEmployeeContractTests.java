package tests.api;

import apiUtils.AuthenticationUtils;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import preconditions.Company;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static dbUtils.DatabaseUtils.deleteCompanyById;
import static dbUtils.DatabaseUtils.deleteEmployeeById;
import static io.restassured.RestAssured.given;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static preconditions.Employee.*;

@DisplayName("Контрактные тесты для Employee")
@Owner("Kseniia Pushina")
@Tag("Api")
public class ApiEmployeeContractTests {
    public static final String URL = "https://x-clients-be.onrender.com";
    static int companyId;
    static int employeeId;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = URL;
        companyId = Company.createCompany();
        employeeId = createEmployee(companyId).jsonPath().getInt("id");
    }

    @AfterEach
    void tearDown() {
        deleteEmployeeById(employeeId);
        deleteCompanyById(companyId);
    }

    @Test
    @DisplayName("Получить список сотрудников для компании — статус 200")
    void getEmployeesByCompany() {
        Response response = given()
                .baseUri(RestAssured.baseURI)
                .accept(ContentType.JSON)
                .queryParam("company", companyId)
                .when()
                .get("/employee");

        var employees = response.jsonPath().getList("");
        assertEquals(200, response.statusCode());
        assertFalse(employees.isEmpty());
    }

    @Disabled("Отключен из-за бага в API: в ответе получаем email null, ожидаем string")
    @Test
    @DisplayName("Тело ответа при получении списка сотрудников по companyID корректно")
    void checkEmployeesResponseBody() {
        Response response = given()
                .baseUri(RestAssured.baseURI)
                .accept(ContentType.JSON)
                .queryParam("company", companyId)  // Параметр компании
                .when()
                .get("/employee");

        List<Map<String, Object>> employees = response.jsonPath().getList("");

        assertFalse(employees.isEmpty());
        employees.forEach(employee -> {
            checkFieldTypes(employee);
            checkKeys(employee);
            checkRequiredFields(employee);
        });
    }

    @Test
    @DisplayName("Создание сотрудника — статус 201")
    void createEmployeeStatus201Test() {
        Response response = createEmployee(companyId);
        response
                .then()
                .statusCode(201);
        int id = response.jsonPath().getInt("id");
        Set<Object> actualKeys = response.jsonPath().getMap("").keySet();
        Set<String> expectedKeys = Set.of("id");

        assertTrue(id > 0);
        assertEquals(expectedKeys, actualKeys);

        deleteEmployeeById(id);
    }

    @Test
    @DisplayName("Получить сотрудника по ID — статус 200")
    void getEmployeeByIdTest() {
        Response response = given()
                .contentType(APPLICATION_JSON.getMimeType())
                .when()
                .get(URL + "/employee/{id}", employeeId);
        int actualId = response.jsonPath().getInt("id");
        assertEquals(200, response.statusCode());
        assertEquals(employeeId, actualId);
    }

    @Disabled("Отключен из-за бага в API: в ответе получаем email null, ожидаем string")
//    ну тут еще с Url проблемы (avatar_url) и в ответе приходит больше полей чем ожидается
    @Test
    @DisplayName("Тело ответа при получении сотрудника по ID корректно")
    void validateEmployeeJson() {
        Response response = given()
                .baseUri(URL)
                .accept(APPLICATION_JSON.getMimeType())
                .when()
                .get("/employee/" + employeeId);

        Map<String, Object> employeeMap = response.jsonPath().getMap("");

        assertEquals(200, response.statusCode());
        checkFieldTypes(employeeMap);
        checkRequiredFields(employeeMap);
        checkKeys(employeeMap);
    }

    @Disabled("Отключен из-за бага в API: /employee/{id} возвращает 200 для несуществующих пользователей")
    @Test
    @DisplayName("Сотрудник не найден по ID — статус 404")
    void getEmployeeNotFoundReturns404() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/employee/0")
                .then()
                .statusCode(404);
    }

    @Disabled("Отключен из-за бага в API: ожидаемый статус код 201 фактический 200")
    @Test
    @DisplayName("Обновление сотрудника - статус 201")
    void updateEmployee() {
        String updatedUrl = "vk.com/id2";
        JSONObject patchBody = new JSONObject();
        patchBody.put("url", updatedUrl);
        given()
                .baseUri(URL)
                .header("x-client-token", AuthenticationUtils.getToken())
                .contentType(APPLICATION_JSON.getMimeType())
                .body(patchBody.toString())
                .when()
                .patch("/employee/" + employeeId)
                .then()
                .statusCode(201);
    }

    @Disabled("Отключен из-за бага в API: ожидаемый статус код 201 фактический 200")
    @Test
    @DisplayName("Тело ответа при обновлении сотрудника корректно")
    void updateEmployeeBody() {
        String updatedUrl = "vk.com/id2";
        JSONObject patchBody = new JSONObject();
        patchBody.put("url", updatedUrl);
        Response patchResponse = given()
                .baseUri(URL)
                .header("x-client-token", AuthenticationUtils.getToken())
                .contentType(APPLICATION_JSON.getMimeType())
                .body(patchBody.toString())
                .when()
                .patch("/employee/" + employeeId);

        Map<String, Object> employeesMap = patchResponse.jsonPath().getMap("");

        assertEquals(201, patchResponse.statusCode());
        checkFieldTypes(employeesMap);
        checkRequiredFields(employeesMap);
        checkKeys(employeesMap);

    }

    @Disabled("Отключен из-за бага в API: ожидаемый статус код 404 фактический 500")
    @Test
    @DisplayName("Статус 404 при обновлении несуществующего сотрудника")
    void returns404IfEmployeeIsNotFoundWhileUpdate() {
        JSONObject patchBody = new JSONObject();
        patchBody.put("url", "vk.com/id2");

        given()
                .baseUri(URL)
                .header("x-client-token", AuthenticationUtils.getToken())
                .contentType(ContentType.JSON)
                .body(patchBody.toString())
                .when()
                .patch("/employee/0")
                .then()
                .statusCode(404);
    }


}