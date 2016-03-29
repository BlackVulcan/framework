package view;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class ContainerView implements View{
	ArrayList<JButton> buttons;
	public ContainerView() {
		JFrame jFrame = new JFrame();
		
		JPanel panel = new JPanel();
		jFrame.getContentPane().add(panel, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<JButton> getButtons(){
		return buttons;
	}
}
