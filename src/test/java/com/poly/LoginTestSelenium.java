package com.poly;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTestSelenium {
    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080";
    
    @BeforeEach
    void setUp() {
        // Khởi tạo WebDriver - không cần đường dẫn nếu WebDriver đã được thêm vào PATH
        driver = new ChromeDriver();
        // Mở cửa sổ trình duyệt với kích thước phù hợp
        driver.manage().window().maximize();
    }
    
    @Test
    void testTC_01_LoginWithValidCredentials() {
        // Mở trang đăng nhập
        driver.get(BASE_URL + "/login");
        
        // Tìm và điền form đăng nhập
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("ân");
        passwordInput.sendKeys("123");
        loginButton.click();
        
        // Đợi chuyển trang
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/index"));
        
        // Kiểm tra kết quả - đã chuyển sang trang chủ
        assertTrue(driver.getCurrentUrl().contains("/index"));
    }
    
    @Test
    void testTC_02_LoginWithInvalidCredentials() {
        driver.get(BASE_URL + "/login");
        
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("mạnh");
        passwordInput.sendKeys("456");
        loginButton.click();
        
        // Đợi và kiểm tra thông báo lỗi với class alert-danger
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("alert-danger")));
            assertTrue(errorMessage.isDisplayed());
            assertTrue(errorMessage.getText().contains("Tên đăng nhập hoặc mật khẩu không đúng"));
        } catch (Exception e) {
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page source: " + driver.getPageSource());
            throw e;
        }
    }
    
    @Test
    void testTC_20_AdminLogin() {
        driver.get(BASE_URL + "/login");
        
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("123");
        loginButton.click();
        
        // Đợi chuyển trang
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/admin/dashboard"));
        
        // Kiểm tra chuyển đến trang admin
        assertTrue(driver.getCurrentUrl().contains("/admin/dashboard"));
    }
    
    @Test
    void testLogout() {
        // Đầu tiên đăng nhập
        driver.get(BASE_URL + "/login");
        
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("ân");
        passwordInput.sendKeys("123");
        loginButton.click();
        
        // Đợi đăng nhập thành công
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/index"));
        
        // Tìm và click nút logout (điều chỉnh selector này theo HTML thực tế của bạn)
        try {
            // Tìm nút đăng xuất (cần điều chỉnh selector theo trang web thực tế)
            WebElement logoutButton = driver.findElement(By.linkText("Đăng xuất"));
            // Hoặc sử dụng các selector khác nếu cần
            // WebElement logoutButton = driver.findElement(By.cssSelector("a.logout"));
            // WebElement logoutButton = driver.findElement(By.id("logout-button"));
            
            logoutButton.click();
            
            // Đợi chuyển về trang đăng nhập
            wait.until(ExpectedConditions.urlContains("/login"));
            assertTrue(driver.getCurrentUrl().contains("/login"));
        } catch (Exception e) {
            System.out.println("Không tìm thấy nút đăng xuất. Kiểm tra HTML của trang.");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page source: " + driver.getPageSource());
            throw e;
        }
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}