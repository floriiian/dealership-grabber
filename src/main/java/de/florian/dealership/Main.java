package de.florian.dealership;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.donedeal.ie/cars?make=Audi").get();
        Elements title = doc.getAllElements();
        for (Element element : title) {
            if(element.hasClass("Pricestyled__Text-sc-1dt81j8-5 hywplu")) {
                if(!element.text().contains("p/m")){
                    LOGGER.info(element.text());
                }
            }
        }
    }
}