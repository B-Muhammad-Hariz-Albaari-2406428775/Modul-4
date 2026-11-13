package id.ac.ui.cs.advprog.eshop.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentDetailFunctionalTest extends BaseFunctionalTest {

    private PaymentTestHelper paymentHelper;
    private OrderTestHelper orderHelper;
    private ProductTestHelper productHelper;

    @BeforeEach
    void setupPaymentTest() {
        paymentHelper = new PaymentTestHelper(baseUrl);
        orderHelper = new OrderTestHelper(baseUrl);
        productHelper = new ProductTestHelper(baseUrl);
    }

    @Test
    void testPaymentDetailFormTitle(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/detail");

        assertEquals("Find Payment", driver.getTitle());
    }

    @Test
    void testPaymentDetailFormHeader(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/detail");

        String header = driver.findElement(By.tagName("h2")).getText();
        assertEquals("Find Payment Details", header);
    }

    @Test
    void testSearchPaymentById(ChromeDriver driver) {
        // Create product, order, and payment
        productHelper.createProduct(driver, "Search Product", "1");
        orderHelper.createOrder(driver, "Charlie", "Search Product");
        String orderId = orderHelper.getFirstOrderId(driver);
        orderHelper.payOrderWithVoucher(driver, orderId, "ESHOP1234ABC5678");
        
        // Extract payment ID from current URL
        String currentUrl = driver.getCurrentUrl();
        String paymentId = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);
        
        // Search for payment
        paymentHelper.searchPaymentById(driver, paymentId);
        
        // Verify payment details are shown
        assertTrue(paymentHelper.paymentExists(driver, paymentId));
        assertTrue(paymentHelper.paymentExists(driver, "VOUCHER"));
    }
}

