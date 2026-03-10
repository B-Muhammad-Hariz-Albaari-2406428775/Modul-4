package id.ac.ui.cs.advprog.eshop.functional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.Objects;

public class PaymentTestHelper {

    private final String baseUrl;

    public PaymentTestHelper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void searchPaymentById(ChromeDriver driver, String paymentId) {
        driver.get(baseUrl + "/payment/detail");
        driver.findElement(By.name("paymentId")).sendKeys(paymentId);
        submitForm(driver);
    }

    public void navigateToAdminPaymentList(ChromeDriver driver) {
        driver.get(baseUrl + "/payment/admin/list");
    }

    public void navigateToAdminPaymentDetail(ChromeDriver driver, String paymentId) {
        driver.get(baseUrl + "/payment/admin/detail/" + paymentId);
    }

    public void updatePaymentStatus(ChromeDriver driver, String paymentId, String status) {
        navigateToAdminPaymentDetail(driver, paymentId);
        
        // Select status
        Select statusSelect = new Select(driver.findElement(By.id("statusSelect")));
        statusSelect.selectByValue(status);
        
        // Submit form
        submitForm(driver);
    }

    public boolean paymentExists(ChromeDriver driver, String text) {
        return Objects.requireNonNull(driver.getPageSource()).contains(text);
    }

    public String getFirstPaymentId(ChromeDriver driver) {
        // Find the first "View Details" button and extract payment ID from its href
        try {
            WebElement viewButton = driver.findElement(By.xpath("//a[contains(@class, 'btn-primary')]"));
            String href = viewButton.getAttribute("href");
            return href.substring(href.lastIndexOf("/") + 1);
        } catch (Exception e) {
            return null;
        }
    }

    private void submitForm(ChromeDriver driver) {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
}

