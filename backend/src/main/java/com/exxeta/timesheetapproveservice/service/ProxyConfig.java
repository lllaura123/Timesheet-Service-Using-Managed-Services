package com.exxeta.timesheetapproveservice.service;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;


@Service
public class ProxyConfig {

    Properties properties = new Properties();
    final String configFile = "config.properties";

    public ProxyConfig() {
        initializeProperties();
    }

    private void initializeProperties() {
        readConfigFile();
        setMissingProperties();
        if (properties.getProperty(ConfigEntry.USE_PROXY.key).equals("true")) {
            System.getProperties().putAll(properties);
        }
        writeToConfigFile();

    }

    private void readConfigFile() {
        if (Files.exists(Paths.get("config.properties"))) {
            try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
                properties.load(fileInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeToConfigFile() {
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            properties.store(fileWriter, "Proxy configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMissingProperties() {
        for (ConfigEntry configEntry : ConfigEntry.values()) {
            if (!properties.containsKey(configEntry.key)) {
                properties.setProperty(configEntry.key, configEntry.value);
            }
        }
    }
}
