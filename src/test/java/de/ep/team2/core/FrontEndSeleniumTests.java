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
     * In this method, the geckodrivers path needs to be edited to one's own path.
     *
     * @return New webdriver
     */
    private WebDriver sysProperties() {
        //Needs to be changed to own path!
        //System.setProperty("webdriver.gecko.driver", "/Users/flex/Downloads/geckodriver");
        WebDriver driver = new FirefoxDriver();
        return driver;
    }

    /**
     * Tests if a normal user can gain access to his own profile and profiles from other users.
     */
    @Test
    public void secureProfileTest() {
        WebDriver driver = sysProperties();

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
        WebDriver driver = sysProperties();

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
     * Test for all functionalities on the page user_data
     */
    @Test
    public void userEnterDataTest() {
        WebDriver driver = sysProperties();

        // Set the wait time of the driver in case of timeouts to 1 second
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        // Login at the website as user
        loginAsUser(driver);

        // Go to page user_data
        driver.navigate().to("http://localhost:8080/user/new");

        // Get the title of the current website
        String currentTitle = driver.getTitle();

        // Check if the correct site loaded by comparing the website title
        assertEquals(currentTitle, "Nutzerdaten eingeben");

        // Enter personal data
        driver.findElement(By.id("firstname")).sendKeys("Benedikt");
        driver.findElement(By.id("lastname")).sendKeys("Schwarz");
        driver.findElement(By.id("gender")).sendKeys("männlich");
        driver.findElement(By.id("age")).sendKeys("12021992");
        driver.findElement(By.id("height")).sendKeys("183");
        driver.findElement(By.id("weight")).sendKeys("80");

        // Continue to the next page
        driver.findElement(By.className("btn-success")).click();
        waitDuration(500);

        // Return to the previous page
        driver.findElement(By.id("btnReturnPage1")).click();
        waitDuration(500);

        // Continue to the next page
        driver.findElement(By.className("btn-success")).click();
        waitDuration(500);

        // Choose single-day-plan
        driver.findElement(By.id("btnSingleDayPlan")).click();
        waitDuration(500);

        // Return to the choice of plan type
        driver.findElement(By.id("btnReturnPage12")).click();
        waitDuration(500);

        // Choose an individual plan
        driver.findElement(By.id("btnIndividualPlan")).click();
        waitDuration(500);

        // Enter that you haven´t trained on a regular basis before
        driver.findElement(By.id("btn1stQuestionNo")).click();
        waitDuration(500);

        // Return to the page to enter your previous experience
        driver.findElement(By.id("btnReturnPage6")).click();
        waitDuration(500);

        // Enter that you have trained on a regular basis before
        driver.findElement(By.id("btn1stQuestionYes")).click();
        waitDuration(500);

        // Enter that you have trained less than 4 months ago
        driver.findElement(By.id("btnPage3Option1")).click();
        waitDuration(500);

        // Enter that you have trained longer than 5 month without a break
        driver.findElement(By.id("btnPage4Option2")).click();
        waitDuration(500);

        // Enter that you have trained two times per week during that time
        driver.findElement(By.id("btnPage5Option2")).click();
        waitDuration(500);

        // Choose endurance focus
        driver.findElement(By.id("btnPage6Option2")).click();
        waitDuration(500);

        // Return to the page to change the chosen focus
        driver.findElement(By.id("btnReturnPage8")).click();
        waitDuration(500);

        // Choose endurance focus
        driver.findElement(By.id("btnPage6Option1")).click();
        waitDuration(500);

        // Continue after reading the description
        driver.findElement(By.id("btnContinuePage7")).click();
        waitDuration(500);

        // Enter that you want to train two times per week
        driver.findElement(By.id("btnPage10Option2")).click();
        waitDuration(500);

        driver.close();
    }

    /**
     * Tests for all functionalities and modals on the page mod_create_plan
     */
    @Test
    public void modCreatePlanTest() {
        WebDriver driver = sysProperties();

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
        driver.findElement(By.id("btnIncrementTEs")).click();
        driver.findElement(By.id("btnIncrementTEs")).click();
        // Check if a 8th input field for repeats has appeared (7 because id-nr starts with 0)
        WebElement inputField = driver.findElement(By.id("repsInput7"));
        assertTrue(inputField.isDisplayed());
        // Decrease the te-amount by 3 to 5 in total
        driver.findElement(By.id("btnDecrementTEs")).click();
        driver.findElement(By.id("btnDecrementTEs")).click();
        driver.findElement(By.id("btnDecrementTEs")).click();
        // Check if inputs after the 5th one have disappeared
        assertTrue(driver.findElements(By.id("repsInput5")).isEmpty());

        /* Test for alert if amount of training units is increased over 15 */
        for(int i=0; i<11; i++) {
            driver.findElement(By.id("btnIncrementTEs")).click();
        }
        // Switch driver to alert window
        driver.switchTo().alert();
        // Accept the alert to close the alert window
        driver.switchTo().alert().accept();
        // Switch driver back to the previous window
        driver.switchTo().defaultContent();

        /* Test for modalConvertPlan */

        // Decrease the te-amount to 1 to trigger the modal
        for(int i=0; i<14; i++) {
            driver.findElement(By.id("btnDecrementTEs")).click();
        }
        // Check if modal to convert the plan to a single day plan has opened
        modal = driver.findElement(By.id("modalConvertPlan"));
        assertTrue(modal.isDisplayed());
        // Close the modal without confirming the conversion
        driver.findElement(By.id("btnCloseModalConvertPlan")).click();
        waitDuration(200);
        // Check if modal closed correctly
        assertFalse(modal.isDisplayed());

        /* Test for alert if amount of training units is decreased below 1 */

        // Try to decrease the amount below 1
        driver.findElement(By.id("btnDecrementTEs")).click();
        // Switch driver to alert window
        driver.switchTo().alert();
        // Accept the alert to close the alert window
        driver.switchTo().alert().accept();
        // Switch driver back to the previous window
        driver.switchTo().defaultContent();

        /* Test for alert if amount of weekly training units is decreased under 1 */
        for(int i=0; i<2; i++) {
            driver.findElement(By.id("btnDecrementWeeklyTes")).click();
        }
        // Switch driver to alert window
        driver.switchTo().alert();
        String alertMessage = driver.switchTo().alert().getText();
        assertTrue(alertMessage.contains("Ein Trainingsplan ist nicht regelmäßig"));
        // Accept the alert to close the alert window
        driver.switchTo().alert().accept();
        // Switch driver back to the previous window
        driver.switchTo().defaultContent();

        /* Test for alert if amount of weekly training units is increased over 7 */
        for(int i=0; i<7; i++) {
            driver.findElement(By.id("btnIncrementWeeklyTes")).click();
        }
        // Switch driver to alert window
        driver.switchTo().alert();
        alertMessage = driver.switchTo().alert().getText();
        assertTrue(alertMessage.contains("kann nicht mehr Trainigseinheiten pro Woche haben"));
        // Accept the alert to close the alert window
        driver.switchTo().alert().accept();
        // Switch driver back to the previous window
        driver.switchTo().defaultContent();

        /* Test for the singleDayCheckbox */

        // Increase the te-amount to 2
        driver.findElement(By.id("btnIncrementTEs")).click();
        // Check the checkbox
        driver.findElement(By.id("singleDayCheck")).click();
        // Check if there are not more than 1 input field for the repeats
        assertTrue(driver.findElements(By.id("repsInput1")).isEmpty());
        // Uncheck the checkbox
        driver.findElement(By.id("singleDayCheck")).click();
        // Check if there are exactly the previous 2 input fields displayed
        assertTrue(driver.findElement(By.id("repsInput1")).isDisplayed());
        assertTrue(driver.findElements(By.id("repsInput2")).isEmpty());

        /* Test for adding an exercise to the plan */
        // Fill out all input fields in order to add the exercise
        driver.findElement(By.id("inputOrder")).sendKeys("B3");
        driver.findElement(By.id("inputExercise")).sendKeys("Klimmzüge");
        driver.findElement(By.id("inputExecution")).sendKeys("Am Platz\n");
        driver.findElement(By.id("repsInput0")).sendKeys("15/20/20");
        driver.findElement(By.id("inputExecution")).sendKeys("Test\n");
        driver.findElement(By.id("repsInput1")).sendKeys("15/15/20/25");
        driver.findElement(By.id("tempoInput0")).sendKeys("60");
        driver.findElement(By.id("tempoInput1")).sendKeys("90");
        driver.findElement(By.id("pauseInput0")).sendKeys("90");
        driver.findElement(By.id("pauseInput1")).sendKeys("60");
        // Add the entered exercise to the plan
        driver.findElement(By.id("buttonAddExercise")).click();

        driver.quit();
    }


    /**
     * Check if user get deleted and if they get redirected correctly.
     */
    @Test
    public void checkDeletionSuccess() {
        WebDriver driver = sysProperties();
        // Login at website as user with email "yannick@gmail.com" and password "123"
        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email"))
                .sendKeys("yannick@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.cssSelector("input.btn")).click();

        // Check User is logged in
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/user/home");

        //locate delete button and delete own profile
        driver.navigate().to("http://localhost:8080/users/");
        driver.findElement(By.id("deleteButton")).click();
        waitDuration(800);
        driver.findElement(By.id("submitForm")).click();
        String url = driver.getCurrentUrl();
        assertTrue(url.equals("http://localhost:8080/login"));

        //check if user can access the website, which he shouldn't be able to
        driver.navigate().to("http://localhost:8080/user/home");
        url = driver.getCurrentUrl();
        assertTrue(url.equals("http://localhost:8080/login"));

        //check if he can log in to the deleted profile
        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email"))
                .sendKeys("yannick@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.cssSelector("input.btn")).click();
        url = driver.getCurrentUrl();
        assertTrue(url.equals("http://localhost:8080/login?error"));

        //check as moderator if the profile is deleted
        loginAsModerator(driver);
        driver.navigate().to("http://localhost:8080/mods/searchuser");
        assertTrue(driver.findElements(By.id("4")).size() == 0);

        driver.quit();

    }

    @Test
    public void editUserData() {
        WebDriver driver = sysProperties();
        loginAsUser(driver);

        //check if data from user gets loaded correctly in input fields
        driver.navigate().to("http://localhost:8080/user/editprofile");
        String value = driver.findElement(By.id("lastnameInput")).getAttribute("value");
        assertTrue(value.equals("Reißig"));
        value = driver.findElement(By.id("firstnameInput")).getAttribute("value");
        assertTrue(value.equals("Alexander"));

        //check if data gets updated correctly
        driver.findElement(By.id("lastnameInput")).clear();
        driver.findElement(By.id("lastnameInput")).sendKeys("Schwarz");
        driver.findElement(By.id("firstnameInput")).clear();
        driver.findElement(By.id("firstnameInput")).sendKeys("Benedikt");
        driver.findElement(By.id("generalSubmit")).click();
        value = driver.getCurrentUrl();
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/users/2");
        value = driver.findElement(By.id("lastName")).getText();
        assertEquals(value, "Schwarz");
        value = driver.findElement(By.id("firstName")).getText();
        assertEquals(value, "Benedikt");

        //check if password gets updated correctly
        driver.navigate().to("http://localhost:8080/user/editprofile");
        driver.findElement(By.id("changePassword")).click();
        waitDuration(200);
        driver.findElement(By.id("oldPassword")).sendKeys("123");
        driver.findElement(By.id("newPassword")).sendKeys("1234");
        driver.findElement(By.id("confirmNewPassword")).sendKeys("1234");
        driver.findElement(By.id("passwordSubmit")).click();
        assertTrue(driver.getPageSource().contains("Altes Passwort ist falsch!"));

        driver.findElement(By.id("changePassword")).click();
        waitDuration(200);
        driver.findElement(By.id("oldPassword")).sendKeys("password");
        driver.findElement(By.id("newPassword")).sendKeys("1234");
        driver.findElement(By.id("confirmNewPassword")).sendKeys("1234");
        driver.findElement(By.id("passwordSubmit")).click();
        assertTrue(driver.getPageSource().contains("Passwort wurde aktualisiert!"));

        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email")).sendKeys("alex@gmail.com");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.cssSelector("input.btn")).click();
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/login?error");
        driver.findElement(By.id("email")).sendKeys("alex@gmail.com");
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.cssSelector("input.btn")).click();
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/user/home");

        //reset data
        driver.navigate().to("http://localhost:8080/user/editprofile");
        driver.findElement(By.id("changePassword")).click();
        waitDuration(200);
        driver.findElement(By.id("oldPassword")).sendKeys("1234");
        driver.findElement(By.id("newPassword")).sendKeys("password");
        driver.findElement(By.id("confirmNewPassword")).sendKeys("password");
        driver.findElement(By.id("passwordSubmit")).click();
        driver.findElement(By.id("lastnameInput")).clear();
        driver.findElement(By.id("lastnameInput")).sendKeys("Reißig");
        driver.findElement(By.id("firstnameInput")).clear();
        driver.findElement(By.id("firstnameInput")).sendKeys("Alexander");
        driver.findElement(By.id("generalSubmit")).click();

        driver.quit();

    }


}
