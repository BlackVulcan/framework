package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Model;
import view.View;

public class Controller implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source instanceof Model){
			//do stuff
		}
		else if(source instanceof View){
			//do stuff
		}
	}

}
