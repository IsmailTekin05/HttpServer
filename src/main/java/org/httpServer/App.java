package org.httpServer;

import org.httpServer.config.Configuration;
import org.httpServer.config.ConfigurationManager;
import org.httpServer.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class App
{

    private final static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        LOGGER.info("Server initializing...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();


        LOGGER.info("Using Port: " + config.getPort() + "\nUsing WebRoot: " + config.getWebroot());

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
