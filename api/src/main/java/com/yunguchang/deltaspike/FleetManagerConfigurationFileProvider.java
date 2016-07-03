package com.yunguchang.deltaspike;

import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.deltaspike.core.spi.config.ConfigSourceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


/**
 * Created by gongy on 2015/10/7.
 */
public class FleetManagerConfigurationFileProvider implements ConfigSourceProvider {


    public static final String FLEET_MANAGER_PROPERTIES = "fleet-manager.properties";

    @Override
    public List<ConfigSource> getConfigSources() {
        List<ConfigSource> configSources = new ArrayList<>();


        File propertiesFile = new File(new File(System.getProperty("jboss.server.config.dir")), FLEET_MANAGER_PROPERTIES);
        if (propertiesFile.exists()) {

            FleetManagerConfigurationFile configFile = new FleetManagerConfigurationFile(propertiesFile);
            configSources.add(configFile);

        }
        return configSources;
    }

    public static class FleetManagerConfigurationFile implements ConfigSource {
        Properties properties = new Properties();

        protected FleetManagerConfigurationFile(File file) {
            try (FileInputStream propertiesFielStream = new FileInputStream(file)){
                properties.load(propertiesFielStream);
            } catch (IOException e) {

            }
        }

        @Override
        public int getOrdinal() {
            return 1000;
        }

        @Override
        public Map<String, String> getProperties() {
            Map<String, String> result = new HashMap<String, String>();
            for (String propertyName : properties.stringPropertyNames()) {
                result.put(propertyName, properties.getProperty(propertyName));
            }

            return result;
        }

        @Override
        public String getPropertyValue(String key) {
            return properties.getProperty(key);
        }

        @Override
        public String getConfigName() {
            return FLEET_MANAGER_PROPERTIES;
        }

        @Override
        public boolean isScannable() {
            return true;
        }
    }
}

