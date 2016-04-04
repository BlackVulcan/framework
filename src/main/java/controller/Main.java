package controller;

import model.Model;

import javax.swing.*;

public class Main {
	public Main(){
		Model model = new Model();
		new Controller(model);
	}

	public static void main(String[] args) {
		new Main();
	}
}
