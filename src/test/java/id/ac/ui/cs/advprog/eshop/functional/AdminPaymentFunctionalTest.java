package id.ac.ui.cs.advprog.eshop.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminPaymentFunctionalTest extends BaseFunctionalTest {

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
    void testAdminPaymentListTitle(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/admin/list");

        assertEquals("Admin - Payment List", driver.getTitle());
    }

    @Test
    void testAdminPaymentListHeader(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/admin/list");

        String header = driver.findElement(By.tagName("h2")).getText();
        assertEquals("Admin - All Payments", header);
    }

    @Test
    void testAdminViewAllPayments(ChromeDriver driver) {
        // Create product, order, and payment
        productHelper.createProduct(driver, "Admin Product", "1");
        orderHelper.createOrder(driver, "Admin User", "Admin Product");
        String orderId = orderHelper.getFirstOrderId(driver);
        orderHelper.payOrderWithVoucher(driver, orderId, "ESHOP1234ABC5678");
        
        // Navigate to admin payment list
        paymentHelper.navigateToAdminPaymentList(driver);
        
        // Verify payment appears in list
        assertTrue(paymentHelper.paymentExists(driver, "VOUCHER"));
    }

    @Test
    void testAdminUpdatePaymentStatus(ChromeDriver driver) {
        // Create product, order, and payment
        productHelper.createProduct(driver, "Status Product", "1");
        orderHelper.createOrder(driver, "Status User", "Status Product");
        String orderId = orderHelper.getFirstOrderId(driver);
        orderHelper.payOrderWithVoucher(driver, orderId, "ESHOP1234ABC5678");
        
        // Extract payment ID from current URL
        String currentUrl = driver.getCurrentUrl();
        String paymentId = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);
        
        // Update payment status
        paymentHelper.updatePaymentStatus(driver, paymentId, "REJECTED");
        
        // Verify status was updated
        assertTrue(paymentHelper.paymentExists(driver, "REJECTED"));
    }
}

