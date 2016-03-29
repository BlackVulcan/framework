package controller;

import model.Model;

public class Main {
	public Main(){
		Model model = new Model();
		Controller controller = new Controller(model);
	}

	public static void main(String[] args) {
		new Main();
	}
}
