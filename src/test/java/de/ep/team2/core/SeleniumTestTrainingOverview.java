package de.ep.team2.core;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.security.Key;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeleniumTestTrainingOverview {
    WebDriver driver;
    String title;


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
     * Function to log into the webpage as user
     */
    public void login(){
        //System.setProperty("webdriver.gecko.driver", "C:\Users\Yannick\Documents");
        driver = new FirefoxDriver();

        //login at website as user with email "alex@gmail.com" and password "hello";
        driver.navigate().to("http://localhost:8080/");
        driver.findElement(By.id("email"))
                .sendKeys("alex@gmail.com");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.cssSelector("input.btn")).click();
    }


    /**
     * Checks if the login is succesfull
     */
    @Test
    public void loginTest() {
        login();
        String url = driver.getCurrentUrl();

        //check if user gets logged in correctly;
        assertTrue(url.equals("http://localhost:8080/user/home"));
    }


    /**
     * Tests features that are part of the user/plan/overview
     */
    @Test
    public void overviewPage(){
        login();
        //check if user can access a Plan
        driver.navigate().to("http://localhost:8080/user/plan/overview");

        title = driver.getTitle();
        assertEquals(title, "Your personalized training page");

        modalCheckOverview();

        driver.findElement(By.id("column")).click();

        title = driver.getTitle();
        assertEquals(title, "In exercise");
        driver.quit();
    }

    /**
     * Checks Modules and warnings.
     */
    public void modalCheckOverview(){
        driver.findElement(By.id("openInfoModal")).click();
        assertTrue(driver.findElement(By.id("infoModal")).isDisplayed());
        waitDuration(800);
        driver.findElement(By.id("leaveInfoModal")).click();
        waitDuration(800);
        assertTrue(!driver.findElement(By.id("infoModal")).isDisplayed());

        driver.findElement(By.id("startButton")).click();

        driver.findElement(By.id("exitOverview")).click();
        assertTrue(driver.findElement(By.id("exitModal")).isDisplayed());
        waitDuration(800);
        driver.findElement(By.id("buttonContinue")).click();
        waitDuration(800);
        assertTrue(!driver.findElement(By.id("exitModal")).isDisplayed());

        driver.findElement(By.id("infoButton")).click();
        assertTrue(driver.findElement(By.id("exerciseModal")).isDisplayed());
        waitDuration(800);
        driver.findElement(By.id("closeInfoModal")).click();
        waitDuration(800);
        assertTrue(!driver.findElement(By.id("exerciseModal")).isDisplayed());
    }


    /**
     * Tests features that are part of the user/plan/exercise
     */
    @Test
    public void exercisePage(){
        login();
        //check if user can access a Plan
        driver.navigate().to("http://localhost:8080/user/plan/");

        title = driver.getTitle();
        assertEquals(title, "In exercise");

        //Start the Training
        String buttonText = driver.findElement(By.id("startButton")).getText();
        assertEquals(buttonText, "Starten");

        modalChecksExercise();
        stopwatch();
        repCounter();

        driver.findElement(By.id("startButton")).click();
        buttonText = driver.findElement(By.id("startButton")).getText();
        assertEquals(buttonText, "Evaluation");
        driver.findElement(By.id("startButton")).click();

        evaluation();

        //Links to correct page on exit
        driver.findElement(By.id("startButton")).click();
        title = driver.getTitle();
        assertEquals(title, "Your personalized training page");

        driver.quit();
    }

    /**
     * Tests features interacting with the Repition counter,
     * like the adjacent buttons and input element.
     */
    public void repCounter(){
        //Test that the right value is preloaded
        String repUser = driver.findElement(By.id("repInput")).getAttribute("value");
        String repStatisch = driver.findElement(By.id("neededReps")).getText();
        assertEquals(repStatisch, repUser);
        int neededRepitions = Integer.parseInt(repStatisch);

        //Can't go below 0
        for(int i = 0; i < neededRepitions +1; i++){
            driver.findElement(By.id("buttonDecrease")).click();
        }
        repUser = driver.findElement(By.id("repInput")).getAttribute("value");
        assertEquals(repUser, "0");

        //Restricted user input
        driver.findElement(By.id("repInput")).sendKeys(Keys.DELETE);
        driver.findElement(By.id("repInput")).sendKeys("777");
        repUser = driver.findElement(By.id("repInput")).getAttribute("value");
        assertEquals(repStatisch, repUser);

        //Can't go to high
        for(int i = 0; i < neededRepitions +1; i++){
            driver.findElement(By.id("buttonIncrease")).click();
        }
        repUser = driver.findElement(By.id("repInput")).getAttribute("value");
        int userRepitions = Integer.parseInt(repUser);
        int maxvalue = 2 * neededRepitions;
        assertEquals(userRepitions, maxvalue);

        //Non valid Input leads to notification module
        driver.findElement(By.id("repInput")).sendKeys(Keys.DELETE);
        driver.findElement(By.id("repInput")).sendKeys("asdnvladsv");
        waitDuration(800);
        driver.findElement(By.id("startButton")).click();
        waitDuration(200);
        assertTrue(driver.findElement(By.id("noValidInputModule")).isDisplayed());
        driver.findElement(By.id("button_noValidInputModule")).click();
        waitDuration(1000);
        assertTrue(!driver.findElement(By.id("noValidInputModule")).isDisplayed());

        //Value reset to default
        driver.findElement(By.id("startButton")).click();
        repUser = driver.findElement(By.id("repInput")).getAttribute("value");
        userRepitions = Integer.parseInt(repUser);
        assertEquals(neededRepitions, userRepitions);
    }


    /**
     * Function to test Stopwatch buttons and display
     */
    public void stopwatch(){
        //Timer starts at 0
        String time = driver.findElement(By.id("time")).getText();
        assertEquals(time, "00:00");
        //Timer can be set
        driver.findElement(By.id("button30s")).click();
        waitDuration(1000);
        assertTrue(!(driver.findElement(By.id("time")).getText()==time));
        //Reset Countdown
        driver.findElement(By.id("buttonStop")).click();
        assertEquals(driver.findElement(By.id("time")).getText(), time);

        //Timer get's properly reset
        driver.findElement(By.id("startButton")).click();
        time = driver.findElement(By.id("time")).getText();
        assertEquals(time, "00:00");
    }


    /**
     * Checks Modules and warnings.
     */
    public void modalChecksExercise(){
        driver.findElement(By.id("infoButton")).click();
        assertTrue(driver.findElement(By.id("exerciseModal")).isDisplayed());
        waitDuration(800);
        driver.findElement(By.id("closeInfoModal")).click();
        waitDuration(800);
        assertTrue(!driver.findElement(By.id("exerciseModal")).isDisplayed());

        driver.findElement(By.id("startButton")).click();
        driver.findElement(By.id("exitExerciseButton")).click();
        assertTrue(driver.findElement(By.id("leavePageModal")).isDisplayed());
        waitDuration(800);
        driver.findElement(By.id("continueButton")).click();
        waitDuration(800);
        assertTrue(!driver.findElement(By.id("leavePageModal")).isDisplayed());

    }

    /**
     * Radio button functionality
     */
    public void evaluation(){
        //TODO Buttons are not getting unselected after another one has been selected
        //1 not selected
        boolean oneNotSelected = driver.findElement(By.id("difficulty1")).isSelected();
        System.out.println("1" + oneNotSelected);
        // 1 selected
        driver.findElement(By.id("difficulty1")).click();
        boolean oneSelected = driver.findElement(By.id("difficulty1")).isSelected();
        System.out.println("1" + oneSelected);
        assertTrue(oneSelected);
        // 3 not selected
        boolean notThreeSelected = driver.findElement(By.id("difficulty3")).isSelected();
        System.out.println("3" + notThreeSelected);
        assertTrue(!notThreeSelected);
        //3 Selected
        driver.findElement(By.id("difficulty3")).click();
        boolean threeSelected = driver.findElement(By.id("difficulty3")).isSelected();
        System.out.println("3" + threeSelected);
        assertTrue(threeSelected);
    }

}