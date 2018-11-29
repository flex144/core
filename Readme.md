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