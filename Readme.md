## Core Workout-Managment application

## Steps to Setup

**1. Clone the repository**

--bash
git clone https://bitbucket.sepws18.padim.fim.uni-passau.de/scm/t2/repo.git

**2. Configure PostgreSQL**

The Project is a Maven project. Select the pom.xml when importing in an IDE.

First create a database either with the default values or use your own.
The Defaults are database_name = "Ep18", username= EpAdmin, password= Hallo123.
Then, open `src/main/resources/application.properties` file and change the spring datasource url, username and password as you defined in PostgreSQL. 

**3. Run the app**

In an IDE run the Main method in the class CoreApplication.
Or Type the following command from the root directory of the project to run it.

--bash
mvn spring-boot:run

**4. File upload**

To enable uploading of images and load them without restarting the server, start the Server with "mvn spring-boot:run".
 
When the server was started this way, in order to start the server again with the IDE (or run tests), run compile in Maven Projects. 
(You can also enable to always compile before building the Project. That solves the Issue.
[Maven Projects -> rightclick on compile -> Execute Before Build])

**5. Run Selenium Frontend Tests**

In order to run the selenium tests, you first need to download the webdriver (for Mozilla Firefox) geckodriver (https://github.com/mozilla/geckodriver/releases/tag/v0.23.0).
Then, open `src/test/java/de/ep/team2/core/FrontEndSeleniumTests.java` and navigate to the method `sysProperties()` and change the path in `System.setProperty()` to the path
of your geckodriver.
Do the same in `src/test/java/de/ep/team2/core/SeleniumTestTrainingOverview.java` in the method `login()`. 
Now you must run the app, and then you can run the Selenium tests. 
               
