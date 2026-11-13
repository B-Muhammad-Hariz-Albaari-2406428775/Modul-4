package id.ac.ui.cs.advprog.eshop.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderHistoryFunctionalTest extends BaseFunctionalTest {

    private OrderTestHelper orderHelper;
    private ProductTestHelper productHelper;

    @BeforeEach
    void setupOrderTest() {
        orderHelper = new OrderTestHelper(baseUrl);
        productHelper = new ProductTestHelper(baseUrl);
    }

    @Test
    void testOrderHistoryTitle(ChromeDriver driver) {
        driver.get(baseUrl + "/order/history");

        assertEquals("Order History", driver.getTitle());
    }

    @Test
    void testViewOrderHistory(ChromeDriver driver) {
        // Create a product and order first
        productHelper.createProduct(driver, "History Product", "5");
        orderHelper.createOrder(driver, "Jane Doe", "History Product");
        
        // View order history
        orderHelper.viewOrderHistory(driver, "Jane Doe");
        
        // Verify order appears in history
        assertTrue(orderHelper.orderExists(driver, "Jane Doe"));
        assertTrue(orderHelper.orderExists(driver, "History Product"));
    }

    @Test
    void testOrderHistoryHeader(ChromeDriver driver) {
        driver.get(baseUrl + "/order/history");

        String header = driver.findElement(By.tagName("h2")).getText();
        assertEquals("View Order History", header);
    }
}

