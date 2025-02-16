package org.httpServer.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.httpServer.util.Json;
import java.io.FileReader;

public class ConfigurationManager {

    private static ConfigurationManager configurationManager;
    private static Configuration configuration;

    private ConfigurationManager(){}

    public static ConfigurationManager getInstance() {
        if(configurationManager == null)
            configurationManager = new ConfigurationManager();

        return configurationManager;
    }

    public void loadConfigurationFile(String filePath) {
        try{
            FileReader fileReader = new FileReader(filePath);
            StringBuffer sb = new StringBuffer();
            int i;
            while((i = fileReader.read())!= -1){
                sb.append((char) i);
            }
            JsonNode conf = Json.parse(sb.toString());
            configuration = Json.fromJson(conf,Configuration.class);
        }catch (Exception e){
            throw new HttpConfigurationException("An Error has occur while loading the configuration.");
        }
    }

    public Configuration getCurrentConfiguration(){
        if(configuration == null){
            throw new HttpConfigurationException("No current configuration set.");
        }
        return configuration;
    }

}
