package de.florian.dealership;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.donedeal.ie/cars?make=Audi").get();
        String title = doc.title();
        LOGGER.debug(title);
    }
}