package controller;

import model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static final Logger LOGGER = LogManager.getFormatterLogger(Main.class);

    private Main(String[] args) throws IOException, InterruptedException {
        Model model = new Model();
        Controller controller = new Controller(model);
        if (args.length >= 3) {
            LOGGER.trace("Auto connecting to: %s on port %s", args[0], args[1]);
            if (controller.connect(args[0], Integer.parseInt(args[1]))) {
                LOGGER.trace("Auto login as %s", args[2]);
                if (controller.login(args[2])) {
                    controller.loadLobby();
                } else {
                    controller.close();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String version = Main.class.getPackage().getImplementationVersion();
        LOGGER.trace("Starting PTGF-Framework");
        LOGGER.trace("Version: " + version);
        LOGGER.trace("Using command line arguments: " + Arrays.toString(args));
        new Main(args);
    }
}
