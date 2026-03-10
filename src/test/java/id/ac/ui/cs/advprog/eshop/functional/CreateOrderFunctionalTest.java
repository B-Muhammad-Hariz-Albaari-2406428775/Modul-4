package id.ac.ui.cs.advprog.eshop.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateOrderFunctionalTest extends BaseFunctionalTest {

    private OrderTestHelper orderHelper;
    private ProductTestHelper productHelper;

    @BeforeEach
    void setupOrderTest() {
        orderHelper = new OrderTestHelper(baseUrl);
        productHelper = new ProductTestHelper(baseUrl);
    }

    @Test
    void testCreateOrder(ChromeDriver driver) {
        // First create a product
        productHelper.createProduct(driver, "Test Product", "10");
        
        // Then create an order with that product
        orderHelper.createOrder(driver, "John Doe", "Test Product");
        
        // Verify order was created
        assertTrue(orderHelper.orderExists(driver, "John Doe"));
        assertTrue(orderHelper.orderExists(driver, "Test Product"));
    }
}

