package org.samo_lego.commandspy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpyConfig {

    // Logger and gson initalizing
    private static final Logger LOGGER = (Logger) LogManager.getLogger();
    // GSON initializing with nice printing options :P
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    public LogConfig logging = new LogConfig();
    public MessageConfig messages = new MessageConfig();
    public List<String> blacklistedCommands = new ArrayList<>(List.of(
            "testCommandThatShouldNotBeLogged"
    ));

    /**
     * Loads config from specified file
     *
     * @param file config file
     * @return config as SpyConfig object
     */
    public static SpyConfig loadConfig(File file) {
        SpyConfig config;

        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                config = gson.fromJson(fileReader, SpyConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("[CommandSpy] An error occured:", e);
            }
        } else {
            config = new SpyConfig();
        }
        config.save(file);

        return config;
    }

    /**
     * Saves config to provided path (file)
     *
     * @param file location where to save config
     */
    private void save(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(this, fileWriter);
        } catch (IOException e) {
            LOGGER.error("[CommandSpy] An error occured:", e);
        }
    }

    public static class LogConfig {

        /**
         * Whether player commands should be logged.
         */
        public boolean logPlayerCommands = true;

        /**
         * Whether commands executed by command blocks should be logged.
         */
        public boolean logCommandBlockCommands = true;

        /**
         * Only log commands executed successfully by command blocks.
         */
        public boolean logCommandBlockWhenSuccessful = true;

        /**
         * Whether commands executed by signs should be logged.
         */
        public boolean logSignCommands = true;

        /**
         * Whether to log the commands to server console.
         */
        public boolean logToConsole = true;

        /**
         * Whether to log the commands to online OPs or any people who are granted permission.
         */
        public boolean logToOps = true;
    }

    public static class MessageConfig {

        /**
         * Message that is sent when command from command block.
         */
        public String commandBlockSuccessMessage = "Command block in: ${dimension} at X: ${x} Y: ${y} Z: ${z} executed command: ${command}";
        public String commandBlockFailedMessage = "Command block in: ${dimension} at X: ${x} Y: ${y} Z: ${z} try to execute command: ${command}";

        /**
         * Message that is sent when player enters command
         */
        public String playerMessage = "Player ${playername} (UUID: ${uuid}) used command: ${command}";

        /**
         * Message that is sent when command from sign is executed
         */
        public String signMessage = "Sign in: ${dimension} at X: ${x} Y: ${y} Z: ${z} executed command: ${command}";
    }
}
