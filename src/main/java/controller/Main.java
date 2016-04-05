package controller;

import model.Model;

public class Main {
	public Main(String[] args) {
		Model model = new Model();
		Controller controller = new Controller(model);
		if (args.length >= 3) {
			if (controller.connect(args[0], Integer.parseInt(args[1]))) {
				if (controller.login(args[2])) {
					controller.loadLobby();
				} else {
					controller.close();
				}
			}
		}
	}

	public static void main(String[] args) {
		new Main(args);
	}
}
