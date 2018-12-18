package de.ep.team2.core;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertTrue;

public class FrontEndSeleniumTests {

    /*
    Tests if a normal user can gain access to his own profile and profiles from other users.
     */
    @Test
    public void secureProfileTest() {
        //Needs to be changed to own path!
        //System.setProperty("webdriver.gecko.driver", "/Users/flex/Downloads/geckodriver");
        WebDriver driver = new FirefoxDriver();

        //login at website as user with email "alex@gmail.com" and password "hello";
        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email"))
                .sendKeys("alex@gmail.com");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.cssSelector("input.btn")).click();

        String title = driver.getCurrentUrl();

        //check if user gets logged in correctly;
        assertTrue(title.equals("http://localhost:8080/user/home"));

        //check if user gets redirected to his personal profile;
        driver.findElement(By.linkText("Profil")).click();
        title = driver.getCurrentUrl();

        assertTrue(title.equals("http://localhost:8080/users/2"));

        //check if user can access a different site than his own;
        driver.navigate().to("http://localhost:8080/users/3");
        title = driver.getPageSource();

        assertTrue(title.contains("Forbidden"));

        driver.quit();
    }

    /*
    Tests if a moderator can look at profiles.
     */
    @Test
    public void modProfileAccessTest() {
        //Needs to be changed to own path!
        //System.setProperty("webdriver.gecko.driver", "/Users/flex/Downloads/geckodriver");
        WebDriver driver = new FirefoxDriver();

        //login at website as moderator with email "felix@gmail.com" and password "123";
        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email"))
                .sendKeys("felix@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.cssSelector("input.btn")).click();

        String title = driver.getCurrentUrl();

        //check if moderator gets logged in correctly;
        assertTrue(title.equals("http://localhost:8080/mods/home"));

        //check if moderator can access user profiles;
        driver.navigate().to("http://localhost:8080/users/4");
        assertTrue(driver.getPageSource().contains("yannick@gmail.com"));

        driver.navigate().to("http://localhost:8080/users/2");
        assertTrue(driver.getPageSource().contains("Alex"));

        driver.navigate().to("http://localhost:8080/users/235");
        assertTrue(driver.getPageSource().contains("Benutzer nicht gefunden"));

        driver.navigate().to("http://localhost:8080/users/felix@gmail.com");
        assertTrue(driver.getPageSource().contains("Felix"));

        driver.quit();

    }
}
