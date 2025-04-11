package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream(".env");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
