package id.ac.ui.cs.advprog.eshop.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PayOrderFunctionalTest extends BaseFunctionalTest {

    private OrderTestHelper orderHelper;
    private ProductTestHelper productHelper;

    @BeforeEach
    void setupOrderTest() {
        orderHelper = new OrderTestHelper(baseUrl);
        productHelper = new ProductTestHelper(baseUrl);
    }

    @Test
    void testPayOrderWithVoucher(ChromeDriver driver) {
        // Create product and order
        productHelper.createProduct(driver, "Payment Product", "3");
        orderHelper.createOrder(driver, "Alice", "Payment Product");
        
        // Get the order ID
        String orderId = orderHelper.getFirstOrderId(driver);
        
        // Pay with voucher
        orderHelper.payOrderWithVoucher(driver, orderId, "ESHOP1234ABC5678");
        
        // Verify payment was processed (redirected to payment detail page)
        assertTrue(driver.getCurrentUrl().contains("/payment/detail/"));
    }

    @Test
    void testPayOrderWithCOD(ChromeDriver driver) {
        // Create product and order
        productHelper.createProduct(driver, "COD Product", "2");
        orderHelper.createOrder(driver, "Bob", "COD Product");
        
        // Get the order ID
        String orderId = orderHelper.getFirstOrderId(driver);
        
        // Pay with COD
        orderHelper.payOrderWithCOD(driver, orderId, "123 Main St", "15000");
        
        // Verify payment was processed (redirected to payment detail page)
        assertTrue(driver.getCurrentUrl().contains("/payment/detail/"));
    }
}

