package de.florian.dealership;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.Objects;

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
        Iterator<JsonNode> ads  = doc.get("props").get("pageProps").get("ads").elements();

        while (ads.hasNext()) {

            JsonNode node = ads.next();
            String[] carInfos = String.valueOf(node.get("keyInfo")).split("[\\[\\]]")[1].split(",");

            int buildYear = Integer.parseInt((carInfos[0].replaceAll("[\",]", "")));
            long carPrice = Long.parseLong((String.valueOf(node.get("price")).replaceAll("[,\"]", "")));
            if ((Objects.equals(String.valueOf(node.get("price")), "null"))) {
                continue;
            }
            String engineType = carInfos[1].replaceAll("[\",]", "");
            String carModel = String.valueOf(node.get("header"));
            String county = String.valueOf(node.get("county"));

            int kmDriven = switch (carInfos.length) {
                case 4 -> Integer.parseInt((carInfos[2] + carInfos[3]).replaceAll("[,\"A-Za-z' ]", ""));
                case 3 -> Integer.parseInt(carInfos[2].replaceAll("[,\"A-Za-z' ]", ""));
                default -> 0;
            };

            try {
                PreparedStatement insert = connection.prepareStatement("INSERT INTO car_prices (car_model,build_year,engine_type,km_driven,car_price,county,timestamp) VALUES (?, ?, ?, ?, ?, ?, ?) ");
                insert.setString(1, carModel);
                insert.setInt(2, buildYear);
                insert.setString(3, engineType);
                insert.setInt(4, kmDriven);
                insert.setLong(5, carPrice);
                insert.setString(6, county);
                insert.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

                insert.execute();
                insert.close();
                LOGGER.debug("Successfully inserted.");

            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }
        connection.close();
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

            try (Statement statement = connection.createStatement())
            {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS car_prices" +
                                "(" +
                                "id SERIAL NOT NULL PRIMARY KEY," +
                                "car_model TEXT  NOT NULL," +
                                "build_year INT  NOT NULL," +
                                "engine_type TEXT  NOT NULL," +
                                "km_driven INT  NOT NULL," +
                                "car_price BIGINT  NOT NULL," +
                                "county TEXT  NOT NULL," +
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