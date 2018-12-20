package de.ep.team2.core;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeleniumTestTrainingOverview {
    WebDriver driver;
    String title;


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

    @Test
    public void loginTest() {
        login();
        String url = driver.getCurrentUrl();

        //check if user gets logged in correctly;
        assertTrue(url.equals("http://localhost:8080/user/home"));
    }

    @Test
    public void overviewPage(){
        login();
        //check if user can access a Plan
        driver.navigate().to("http://localhost:8080/user/plan/overview");

        title = driver.getTitle();
        assertEquals(title, "Your personalized training page");

        //TODO modal and linking
    }


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
        driver.findElement(By.id("startButton")).click();

        //Go to Evaluation
        driver.findElement(By.id("startButton")).click();
        stopwatch();
        // repCounter();

        /*
        buttonText = driver.findElement(By.id("startButton")).getText();
        assertEquals(buttonText, "Evaluation");
        driver.findElement(By.id("startButton")).click();


        //Check Radio Buttons
        evaluation();


        modalChecks();

        //Links to correct page on exit
        driver.findElement(By.id("startButton")).click();
        title = driver.getTitle();
        assertEquals(title, "Your personalized training page");
        */
    }

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

        /* TODO show Module opening on invalid input like "öasldkf" */

        //Value reset to default
        driver.findElement(By.id("startButton")).click();
        repUser = driver.findElement(By.id("repInput")).getAttribute("value");
        System.out.println(repUser + "_user" + " " + repStatisch + "_statisch");
        userRepitions = Integer.parseInt(repUser);
        assertEquals(neededRepitions, userRepitions);
    }

    public void stopwatch(){
        //Timer starts at 0
        String time = driver.findElement(By.id("time")).getText();
        assertEquals(time, "00:00");
        //Timer can be set
        driver.findElement(By.id("button30s")).click();
        //TODO Thread.wait
        assertTrue(!(driver.findElement(By.id("time")).getText()==time));
        //Reset Countdown
        driver.findElement(By.id("buttonRecommended")).click();
        assertEquals(driver.findElement(By.id("time")).getText(), time);

        //Timer get's properly reset
        driver.findElement(By.id("startButton")).click();
        time = driver.findElement(By.id("time")).getText();
        assertEquals(time, "00:00");
    }


    public void modalChecks(){

    }

    public void evaluation(){
        //TODO Buttons are not getting unselected after another one has been selected
        //3 Selected
        driver.findElement(By.id("difficulty3")).click();
        boolean threeSelected = driver.findElement(By.id("difficulty3")).isSelected();
        System.out.println("3" + threeSelected);
        assertTrue(threeSelected);

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
    }
}
