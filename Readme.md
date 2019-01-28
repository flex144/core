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

On Default an admin user is created with the login-information: email: '"admin@ep18.com"' password: 'QxA4cxKWAT2bwmsD'.
This values can be changed in the class `src/main/java/de/ep/team2/core/dataInit/DataInit.java`.

The Database can be filled with example Data. This can also be enabled in the class DataInit.java.
The first three Variables of the class are used to set these values.

**3. Run the app**

In an IDE run the Main method in the class CoreApplication.
Or Type the following command from the root directory of the project to run it.

--bash
mvn spring-boot:run

**4. File upload when starting with IDE**

To enable uploading of images and load them without restarting the server, start the Server with "mvn spring-boot:run".
 
When the server was started this way, in order to start the server again with the IDE (or run tests), run compile in Maven Projects. 
(You can also enable to always compile before building the Project. That solves the Issue.
[Maven Projects -> rightclick on compile -> Execute Before Build])

**5. Deploy on Tomcat**

By default the Application is configured to be run in the IDE with the integrated Tomcat-server of Spring.
If you want to deploy the Application as a war on a seperate Tomcat-server, two slight changes have to be made.
1. Change the value of the Attribute 'deployOnTomcat' in the class `src/main/java/de/ep/team2/core/service/ExerciseService.java` to 'true'.
This changes the Filepath of the images to the required value.
2. In the Xml-File `src/main/resources/logback-spring.xml` change the Path-values as written in the Comments of the File. This changes the path of the Logging-file to the required location.
(If you want to run the System in the IDE again you have to revert this changes.)
Now execute the Maven Goal "clean package", the project should build and in the target folder a war file named 'team2.war' should be created.
Use this File to Deploy the Application on the Tomcat.

**6. Tests**

The Unit Tests are designed to be run on a Database with just the Example Data in it. So when you want to Run the Tests go to the class
`src/main/java/de/ep/team2/core/dataInit/DataInit.java` and set the attributes 'fillWithExampleData' and 'alwaysClearDb' to true.
(This will delete everthing in the DB.)

-- Run Selenium Frontend Tests

In order to run the selenium tests, you first need to download the webdriver (for Mozilla Firefox) geckodriver (https://github.com/mozilla/geckodriver/releases/tag/v0.23.0).
Then, open `src/test/java/de/ep/team2/core/FrontEndSeleniumTests.java` and navigate to the method `sysProperties()` and change the path in `System.setProperty()` to the path
of your geckodriver.
Do the same in `src/test/java/de/ep/team2/core/SeleniumTestTrainingOverview.java` in the method `login()`. 
Now you must run the app, and then you can run the Selenium tests. 
               
