package de.florian.dealership;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParser.*;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException, SQLException {

        Connection connection = connectToDatabase("postgres", "Creeper008");

        String carName = "Audi";
        Document document = Jsoup.connect("https://www.donedeal.ie/cars?make=" + carName).get();

        String data = Objects.requireNonNull(document.getElementById("__NEXT_DATA__")).data();

        // LOGGER.debug(document2);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode doc = objectMapper.readTree(data);
        LOGGER.debug(doc);






        /*

                Elements websiteElements = document.getAllElements();


        for (Element element : websiteElements) {
            if (element.hasClass("Pricestyled__Text-sc-1dt81j8-5 hywplu")) {
                if (!element.text().contains("p/m") && !element.text().contains("€0") && !element.text().equals("No Price")){
                    try {
                        PreparedStatement insert = connection.prepareStatement("INSERT INTO car_prices (car_name,car_price,timestamp) VALUES (?, ?, ?) ");

                        insert.setString(1, carName);
                        insert.setLong(2, Long.parseLong(element.text().replaceAll("[£€,]", "")));
                        insert.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

                        insert.execute();
                        insert.close();

                        LOGGER.debug("Inserted {} for {}", element.text(), carName);

                    }catch(SQLException e){
                        LOGGER.error(e);
                    }
                }
            }
        }
        connection.close();
    */
    }
    public static Connection connectToDatabase(String username, String password){

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", username, password);
            LOGGER.info("Database connection initialized.");

            try (Statement statement = connection.createStatement())
            {
                statement.executeUpdate("CREATE DATABASE dealership_prices");
                LOGGER.debug("Database: \" dealership_prices\" created successfully.");
            }
            catch (SQLException e)
            {
                LOGGER.debug("Database already exists. Skipping.");
            }

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dealership_prices", username, password);
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement())
            {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS car_prices" +
                                "(" +
                                "id SERIAL NOT NULL PRIMARY KEY," +
                                "car_name TEXT  NOT NULL," +
                                "car_price BIGINT  NOT NULL," +
                                "timestamp TIMESTAMP NOT NULL"
                                + ")"
                );
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e);
            System.exit(0);
        }

        return connection;
    }
}