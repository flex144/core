package de.ep.team2.core;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontEndSeleniumTests {

    /**
     * Function to navigate the driver to the website and login as moderator with email:
     * "felix@gmail.com" and password "123"
     * @param driver Driver to navigate to the website and to login as moderator
     */
    private void loginAsModerator(WebDriver driver) {
        // Login at website as moderator with email "felix@gmail.com" and password "123";
        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email"))
                .sendKeys("felix@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.cssSelector("input.btn")).click();

        // Check Moderator is logged in
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/mods/home");
    }

    /**
     * Function to navigate the driver to the website and login as user with email: "alex@gmail.com"
     * and password "password"
     * @param driver Driver to navigate to the website and to login as user
     */
    private void loginAsUser(WebDriver driver) {
        // Login at website as user with email "alex@gmail.com" and password "password"
        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email"))
                .sendKeys("alex@gmail.com");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.cssSelector("input.btn")).click();

        // Check User is logged in
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/user/home");
    }

    /**
     * Function to wait a certain time continue
     * @param durationInMs Amount of milliseconds to wait
     */
    private void waitDuration(long durationInMs) {
        try {
            Thread.sleep(durationInMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if a normal user can gain access to his own profile and profiles from other users.
     */
    @Test
    public void secureProfileTest() {
        //Needs to be changed to own path!
        //System.setProperty("webdriver.gecko.driver", "/Users/flex/Downloads/geckodriver");
        WebDriver driver = new FirefoxDriver();

        // Login at website as user
        loginAsUser(driver);

        //check if user gets redirected to his personal profile;
        driver.findElement(By.linkText("Profil")).click();
        String title = driver.getCurrentUrl();

        assertEquals(title, "http://localhost:8080/users/2");

        //check if user can access a different site than his own;
        driver.navigate().to("http://localhost:8080/users/3");
        title = driver.getPageSource();

        assertTrue(title.contains("Forbidden"));

        driver.quit();
    }

    /**
     * Tests if a moderator can look at profiles.
     */
    @Test
    public void modProfileAccessTest() {
        //Needs to be changed to own path!
        //System.setProperty("webdriver.gecko.driver", "/Users/flex/Downloads/geckodriver");
        WebDriver driver = new FirefoxDriver();

        // Login at website as moderator
        loginAsModerator(driver);

        // Check if moderator can access user profiles
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

    /**
     * Tests for all functionalities and modals on the page mod_create_plan
     */
    @Test
    public void modCreatePlanTest() {
        // Create new istance of Firefox driver
        WebDriver driver = new FirefoxDriver();

        // Set the wait time of the driver in case of timeouts to 1 second
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        // Login at the website as moderator
        loginAsModerator(driver);

        // Go to page mod_create_plan
        driver.navigate().to("http://localhost:8080/mods/createplan");

        // Get the title of the current website
        String currentTitle = driver.getTitle();

        // Check if the correct site loaded by comparing the website title
        assertEquals(currentTitle, "Planvorlage erstellen");

        /* Test for modalExerciseForm */

        // Open modal to create a new exercise
        driver.findElement(By.id("buttonCreateExercise")).click();
        // Check if the correct modal opened
        WebElement modal = driver.findElement(By.id("modalExerciseForm"));
        assertTrue(modal.isDisplayed());
        // Close the modal
        driver.findElement(By.id("buttonCloseExerciseForm")).click();
        waitDuration(200);
        // Check if the modal closed correctly
        assertFalse(modal.isDisplayed());

        /* Test for modalExecutionForm */

        // Open modal to create a new type of execution
        driver.findElement(By.id("buttonNewExecution")).click();
        // Check if the correct modal opened
        modal = driver.findElement(By.id("modalExecutionForm"));
        assertTrue(modal.isDisplayed());
        // Close the modal
        driver.findElement(By.id("buttonCloseExecutionModal")).click();
        waitDuration(200);
        // Check if the modal closed correctly
        assertFalse(modal.isDisplayed());

        /* Test for modalConfirmResetForm */

        // Fill out the order input field in order to test the reset form function and confirmation modal
        driver.findElement(By.id("inputOrder")).sendKeys("A1");
        // Open modal to confirm the reset of the input form
        driver.findElement(By.id("buttonResetModal")).click();
        // Check if the correct modal with the confirmation of the form reset opened
        modal = driver.findElement(By.id("modalConfirmResetForm"));
        assertTrue(modal.isDisplayed());
        // Confirm the reset and close the confirmation modal
        driver.findElement(By.id("buttonResetFormular")).click();
        waitDuration(200);
        // Check if the modal closed correctly
        assertFalse(modal.isDisplayed());
        // Check if the input form has been reset
        String inputValue = driver.findElement(By.id("inputOrder")).getText();
        assertFalse(inputValue.contains("A1"));

        /* Test for changing amount of TE´s */

        // Increase the te-amount by 2 to 8 in total (6 is default te value)
        driver.findElement(By.id("btnIncrement")).click();
        driver.findElement(By.id("btnIncrement")).click();
        // Check if a 8th input field for repeats has appeared (7 because id-nr starts with 0)
        WebElement inputField = driver.findElement(By.id("repeatsInput7"));
        assertTrue(inputField.isDisplayed());
        // Decrease the te-amount by 3 to 5 in total
        driver.findElement(By.id("btnDecrement")).click();
        driver.findElement(By.id("btnDecrement")).click();
        driver.findElement(By.id("btnDecrement")).click();
        // Check if inputs after the 5th one have disappeared
        assertTrue(driver.findElements(By.id("repeatsInput5")).isEmpty());

        /* Test for modalConvertPlan */

        // Decrease the te-amount to 1 to trigger the modal
        driver.findElement(By.id("btnDecrement")).click();
        driver.findElement(By.id("btnDecrement")).click();
        driver.findElement(By.id("btnDecrement")).click();
        driver.findElement(By.id("btnDecrement")).click();
        // Check if modal to convert the plan to a single day plan has opened
        modal = driver.findElement(By.id("modalConvertPlan"));
        assertTrue(modal.isDisplayed());
        // Close the modal without confirming the conversion
        driver.findElement(By.id("btnCloseModalConvertPlan")).click();
        waitDuration(200);
        // Check if modal closed correctly
        assertFalse(modal.isDisplayed());

        /* Test for the singleDayCheckbox */

        // Increase the te-amount to 2
        driver.findElement(By.id("btnIncrement")).click();
        // Check the checkbox
        driver.findElement(By.id("singleDayCheck")).click();
        // Check if there are not more than 1 input field for the repeats
        assertTrue(driver.findElements(By.id("repeatsInput1")).isEmpty());
        // Uncheck the checkbox
        driver.findElement(By.id("singleDayCheck")).click();
        // Check if there are exactly the previous 2 input fields displayed
        assertTrue(driver.findElement(By.id("repeatsInput1")).isDisplayed());
        assertTrue(driver.findElements(By.id("repeatsInput2")).isEmpty());

        /* Test for adding an exercise to the plan */
        // Fill out all input fields in order to add the exercise
        driver.findElement(By.id("inputOrder")).sendKeys("B3");
        driver.findElement(By.id("inputExercise")).sendKeys("Klimmzüge");
        driver.findElement(By.id("inputExecution")).sendKeys("Am Platz\n");
        driver.findElement(By.id("repeatsInput0")).sendKeys("15/20/20");
        driver.findElement(By.id("inputExecution")).sendKeys("Test\n");
        driver.findElement(By.id("repeatsInput1")).sendKeys("15/15/20/25");
        driver.findElement(By.id("tempoInput0")).sendKeys("60");
        driver.findElement(By.id("tempoInput1")).sendKeys("90");
        driver.findElement(By.id("pauseInput0")).sendKeys("90");
        driver.findElement(By.id("pauseInput1")).sendKeys("60");
        // Add the entered exercise to the plan
        driver.findElement(By.id("buttonAddExercise")).click();
        // Check if input form has been reset
        inputValue = driver.findElement(By.id("inputOrder")).getText();
        assertFalse(inputValue.contains("B3"));
        // Check if exercise was added
        assertFalse(driver.findElements(By.id("planExerciseOrder")).isEmpty());

        /* Test for modalDetailsExercise */

        // Open modal to show the details of an added exercise
        driver.findElement(By.xpath(".//*[text()[contains(.,\"Details\")]]")).click();
        // Check if the correct modal opened
        modal = driver.findElement(By.id("modalDetailsExercise"));
        assertTrue(modal.isDisplayed());
        // Close the modal
        driver.findElement(By.id("btnCloseModalShowDetails")).click();
        waitDuration(200);
        // Check if the modal closed correctly
        assertFalse(modal.isDisplayed());

        /* Test for modalViewFinalPlan */

        // Open modal to show a preview of the final plan
        driver.findElement(By.id("previewPlan")).click();
        // Check if the correct modal opened
        modal = driver.findElement(By.id("modalViewFinalPlan"));
        assertTrue(modal.isDisplayed());
        // Close the modal
        driver.findElement(By.id("btnCloseModalViewPlan")).click();
        waitDuration(200);
        // Check if the modal closed correctly
        assertFalse(modal.isDisplayed());

        driver.quit();
    }
}
