package com.POJOClasses.config;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ApiConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("api.base.url");
    }

    public static String getApiKey() {
        return properties.getProperty("api.key");
    }

    public static String getApiToken() {
        return properties.getProperty("api.token");
    }
}