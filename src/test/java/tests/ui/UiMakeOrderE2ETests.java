package tests.ui;

import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.PageCart;
import pages.PageInventory;
import pages.PageLogin;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

@DisplayName("E2E Покупка товаров")
@Story("Покупка 3 товаров пользователями standard_user и performance_glitch_user")
@Owner("Kseniia Pushina")
@Tag("Ui")
public class UiMakeOrderE2ETests {

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

    @ParameterizedTest(name = "пользователем: {0}")
    @ValueSource(strings = {"standard_user", "glitch_user"})
    @DisplayName("E2E заказ трёх товаров")
    @ExtendWith(AllureJunit5.class)
    public void e2ePurchase(String userKey) {
        String username = ConfigReader.get(userKey);
        String password = ConfigReader.get("passwordUI");
        new PageLogin(driver)
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();

        new PageInventory(driver)
                .addToCart("Sauce Labs Backpack")
                .addToCart("Sauce Labs Bolt T-Shirt")
                .addToCart("Sauce Labs Onesie")
                .goToCart();

        new PageCart(driver)
                .verifyItemsInCart(3)
                .clickCheckout()
                .fillCheckoutForm("Павел", "Дуров", "4450702")
                .clickContinue()
                .verifyTotal("$58.29")
                .clickFinish();
    }
}
