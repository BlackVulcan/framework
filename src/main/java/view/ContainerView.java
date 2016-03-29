package view;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;

public class ContainerView extends View{
	ArrayList<JButton> buttons;

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<JButton> getButtons(){
		return buttons;
	}
}
