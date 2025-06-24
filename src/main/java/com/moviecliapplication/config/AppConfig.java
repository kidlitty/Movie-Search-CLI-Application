package com.moviecliapplication.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private final Properties properties;

    public AppConfig() {
        properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getTmdbApiKey() {
        String apiKey = properties.getProperty("tmdb.api.key");
        return (apiKey != null) ? apiKey.trim() : null;
    }
}
