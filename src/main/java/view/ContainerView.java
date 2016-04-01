package view;

import javax.swing.*;

import model.Model;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ContainerView extends JFrame implements View {
	private static final long serialVersionUID = 1L;
	private JPanel container;
	private ArrayList<JButton> buttons = new ArrayList<>();
	private JLabel turn, opponent, time;

	public ContainerView() {
		container = new JPanel();
		container.setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(new JScrollPane(container), BorderLayout.CENTER);

		JPanel informationPanel = new JPanel();
		getContentPane().add(informationPanel, BorderLayout.NORTH);
		informationPanel.setLayout(new GridLayout(0, 3, 0, 0));
		turn = opponent = time = new JLabel();
		informationPanel.add(turn);
		informationPanel.add(opponent);
		informationPanel.add(time);
		setFullScreen();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 300));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		if(object instanceof Model){
			Model model = (Model) object;
			if(e.getID() == Model.TURN_SWITCHED){
				setTurn(model.getTurn());
			}
		}

	}

	public ArrayList<JButton> getButtons() {
		return buttons;
	}

	public void showView(Component component) {
		container.removeAll();
		container.add(component);
	}

	public void setFullScreen(){
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void setTurn(Boolean myTurn){
		System.out.println("setting turn");
		String turnInformation = "";
		if(myTurn)
			turnInformation = "It's your turn!";
		else
			turnInformation = "Wait for opponent!";
		this.turn.setText(turnInformation);
	}

	public void setTurnEmpty(){
		this.turn.setText("");
	}

	private void setTimeBox(String time){
		this.time.setText(time);
	}

	public void setTime(int timeInMilis, Model model){
		Runnable thread = new Runnable(){
			public void run(){
				int timeInTens = timeInMilis / 100;
				boolean timeIsRunning = model.getTurn();
				while(timeIsRunning){
					for(int i = timeInTens; i >= 0; i--){
						if(model.getTurn()){
							try {
								setTimeBox("Seconds left: " + (i/10) + "." + (i%10));
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						else{
							timeIsRunning = false;
							break;
						}
					}
					if(timeIsRunning){
						timeIsRunning = false;
						setTimeBox("Time has run out");
					}
					else
						setTurnEmpty();
				}
			}        
		};
		new Thread(thread).start();
	}
}
