# Dealership Grabber
This program will grab a list of car data and insert it into your database, might be useful for finding the avergage 
or lowest available price of a car.

## How to use
- Download the program
- Download PostgreSQL: https://www.postgresql.org/
- Install PostgreSQL and change the port inside the program to the port your PostgreSQL server is running on:
- Example:
```java
"jdbc:postgresql://localhost:5432/"
```
- Change the following line inside the program to your servers credentials:
```java
Connection connection = prepareDatabase("postgres", "yourpassword");
```
- Make sure your PostgreSQL server is up and running, now start the program and try if it successfully connect to your server.
  If you see: "``Database connection initialized.``" appearing in the console, you're all set.
  
- You will have to specify which kind of car brand you want to search for here:
```java
 String carName = "Ford";
````
- Now run the program and let's see if it works, you should now see your data flowing
  into your database.

