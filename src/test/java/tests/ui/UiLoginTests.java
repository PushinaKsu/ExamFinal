package tests.ui;

import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.PageInventory;
import pages.PageLogin;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

@DisplayName("Авторизация")
@Story("Авторизация пользователей")
@Owner("Kseniia Pushina")
@Tag("Ui")
class UiLoginTests {

    WebDriver driver;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        driver = new ChromeDriver(options);
        driver.get("https://www.saucedemo.com/");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulLoginTest() {
        String username = ConfigReader.get("standard_user");
        String password = ConfigReader.get("passwordUI");

        new PageLogin(driver)
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();

        new PageInventory(driver)
                .verifyOnInventoryPage();
    }

    @Test
    @DisplayName("Авторизация заблокированного пользователя")
    public void blockedUserLoginTest() {
        String username = ConfigReader.get("locked_user");
        String password = ConfigReader.get("passwordUI");

        new PageLogin(driver)
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();
        String errorMessage = driver.findElement(By.xpath("//h3[@data-test='error']")).getText();
        checkError(errorMessage);
    }

    @Step("Проверка, что показано сообщение об ошибке: «{expected}»")
    void checkError(String actual) {
        Assertions.assertEquals("Epic sadface: Sorry, this user has been locked out.", actual, "Сообщение об ошибке не совпадает.");
    }

}