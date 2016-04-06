package controller;

import model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Main {
	public static String version;
	public static Logger logger = LogManager.getFormatterLogger(Main.class);

	private Main(String[] args) {
		Model model = new Model();
		Controller controller = new Controller(model);
		if (args.length >= 3) {
			logger.trace("Auto connecting to: %s on port %s", args[0], args[1]);
			if (controller.connect(args[0], Integer.parseInt(args[1]))) {
				logger.trace("Auto login as %s", args[2]);
				if (controller.login(args[2])) {
					controller.loadLobby();
				} else {
					controller.close();
				}
			}
		}
	}

	public static void main(String[] args) {
		version = Main.class.getPackage().getImplementationVersion();
		logger.trace("Starting PTGF-Framework");
		logger.trace("Version: " + version);
		logger.trace("Using command line arguments: " + Arrays.toString(args));
		new Main(args);
	}
}
