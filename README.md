# IT5100A-Project
## Introduction
* This version aims to explore the idea of ***functional programming*** in scala
* Based on the initial version using ***scala slick*** to perform some query with mySQL, we add some additional features into it
* Instead of using imperative commands, we try to apply ***cats.effect.IO*** and ***Either[Left,Right]*** to avoid side effects and handle the exception
* Also, we use the idea of ***OOP*** to make some code reusable

## Configuration
* Before entering ```sbt run``` in the terminal, the database configuration file needs to be modified so that the program can communicate with the mySQL on the local machine:
    * Find /src/main/resources/application.properties
    * Modified following items to fit your machine:
        * url = jdbc:mysql://127.0.0.1/*databasename*
        * user = *username*
        * password = *password*