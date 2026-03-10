package id.ac.ui.cs.advprog.eshop.functional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.Objects;

public class OrderTestHelper {

    private final String baseUrl;

    public OrderTestHelper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void createOrder(ChromeDriver driver, String authorName, String productName) {
        driver.get(baseUrl + "/order/create");
        
        // Find and check the checkbox for the product
        String xpath = String.format("//tr[td[contains(text(), '%s')]]//input[@type='checkbox']", productName);
        WebElement checkbox = driver.findElement(By.xpath(xpath));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        
        // Enter author name
        driver.findElement(By.name("author")).sendKeys(authorName);
        
        // Submit form
        submitForm(driver);
    }

    public void viewOrderHistory(ChromeDriver driver, String authorName) {
        driver.get(baseUrl + "/order/history");
        driver.findElement(By.name("author")).sendKeys(authorName);
        submitForm(driver);
    }

    public void navigateToPaymentPage(ChromeDriver driver, String orderId) {
        driver.get(baseUrl + "/order/pay/" + orderId);
    }

    public void payOrderWithVoucher(ChromeDriver driver, String orderId, String voucherCode) {
        navigateToPaymentPage(driver, orderId);
        
        // Select payment method
        Select paymentMethodSelect = new Select(driver.findElement(By.id("paymentMethodSelect")));
        paymentMethodSelect.selectByValue("VOUCHER");
        
        // Enter voucher code
        driver.findElement(By.name("voucherCode")).sendKeys(voucherCode);
        
        // Submit form
        submitForm(driver);
    }

    public void payOrderWithCOD(ChromeDriver driver, String orderId, String address, String deliveryFee) {
        navigateToPaymentPage(driver, orderId);
        
        // Select payment method
        Select paymentMethodSelect = new Select(driver.findElement(By.id("paymentMethodSelect")));
        paymentMethodSelect.selectByValue("CASH_ON_DELIVERY");
        
        // Enter address and delivery fee
        driver.findElement(By.name("address")).sendKeys(address);
        driver.findElement(By.name("deliveryFee")).sendKeys(deliveryFee);
        
        // Submit form
        submitForm(driver);
    }

    public boolean orderExists(ChromeDriver driver, String text) {
        return Objects.requireNonNull(driver.getPageSource()).contains(text);
    }

    public String getFirstOrderId(ChromeDriver driver) {
        // Find the first "Pay Now" button and extract order ID from its href
        try {
            WebElement payButton = driver.findElement(By.xpath("//a[contains(@class, 'btn-success')]"));
            String href = payButton.getAttribute("href");
            return href.substring(href.lastIndexOf("/") + 1);
        } catch (Exception e) {
            return null;
        }
    }

    private void submitForm(ChromeDriver driver) {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
}

