package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import model.Model;
import view.ContainerView;
import view.View;

public class Controller implements ActionListener{
	ContainerView containerView;
	public Controller(Model model) {
		this.containerView = new ContainerView();
		
		//add actionListeners to control buttons
		for(JButton button: containerView.getButtons()){
			button.addActionListener(this);
		}
	}

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
