package view;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class ContainerView extends JFrame implements View{
	private static final long serialVersionUID = 1L;
	JPanel container;
	ArrayList<JButton> buttons = new ArrayList<>();
	public ContainerView() {		
		container = new JPanel();
		this.getContentPane().add(container, BorderLayout.CENTER);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<JButton> getButtons(){
		return buttons;
	}
	
	public void showView(JPanel panel){
		getContentPane().add(panel, BorderLayout.CENTER);
	}
}
