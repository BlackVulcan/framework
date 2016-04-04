package view;

import javax.swing.*;

import model.Model;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ContainerView extends JFrame implements View {
	private static final long serialVersionUID = 1L;
	private static final String ICON_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "gameicon.png";
	private JPanel container;
	private ArrayList<JButton> buttons = new ArrayList<>();
	private JLabel turn, turnMessage, opponent, time;

	public ContainerView() {
		super("Two player game framework");
		ImageIcon img = new ImageIcon(ICON_PATH);
		this.setIconImage(img.getImage());
		
		container = new JPanel();
		container.setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(new JScrollPane(container), BorderLayout.CENTER);

		JPanel informationPanel = new JPanel();
		getContentPane().add(informationPanel, BorderLayout.NORTH);
		informationPanel.setLayout(new GridLayout(0, 3, 0, 0));
		opponent = new JLabel("", SwingConstants.CENTER);
		turn = new JLabel("", SwingConstants.CENTER);
		turnMessage = new JLabel("", SwingConstants.CENTER);
		time = new JLabel("", SwingConstants.CENTER);
		informationPanel.add(opponent);
		informationPanel.add(turn);
		informationPanel.add(time);
		setFullScreen();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 300));
	}
	
	public static String pathComponent(String filename) {
	      int i = filename.lastIndexOf(File.separator);
	      return (i > -1) ? filename.substring(0, i) : filename;
	  }

	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		int objectID = e.getID();
		if(object instanceof Model){
			Model model = (Model) object;
			if(objectID == Model.TURN_SWITCHED){
				setTurn(model.getTurn());
			}
			else if(objectID == Model.GAME_DRAW){
				this.turn.setText("It's a draw!");
			}
			else if(objectID == Model.GAME_LOSS){
				System.out.println("You NOOB! You have lost the game!");
				this.turn.setText("You have lost the game!");
			}
			else if(objectID == Model.GAME_WIN){
				this.turn.setText("You have won the game!");
			}
			else if(objectID == Model.GAME_CHANGED && e.getActionCommand().equals(Model.OPPONENT_SET)){
				this.opponent.setText("Opponent: " + model.getOpponent());
			}
			else if(objectID == Model.TURN_MESSAGE_CHANGED){
				this.setTurnMessage(model.getTurnMessage());
			}
		}
	}

	public ArrayList<JButton> getButtons() {
		return buttons;
	}

	public void showView(Component component) {
		container.removeAll();
		container.add(component);
		revalidate();
	}

	public void setFullScreen(){
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void setTurn(Boolean myTurn){
		System.out.println("setting turn");
		String turnInformation = "";
		if(myTurn)
			turnInformation = "Your turn!";
		else
			turnInformation = opponent.getText() + "'s turn!";
		this.turn.setText(turnInformation);
	}

	public void setTurnEmpty(){
		this.turn.setText("");
	}
	
	public void setTurnMessage(String message){
		this.turnMessage.setText(message);
	}

	private void setTimeBox(String time){
		this.time.setText(time);
	}

	public void setTime(int timeInMilis, Model model){
		System.out.println("time is set");
		Runnable thread = new Runnable(){
			public void run(){
				setTimeBox("");
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
							setTimeBox("");
							break;
						}
					}
					if(timeIsRunning){
						timeIsRunning = false;
						setTimeBox("Time has run out");
					}
					else
						setTimeBox("");
				}
			}        
		};
		new Thread(thread).start();
	}
	
	public void reset(){
		this.turn.setText("");
		this.time.setText("");
		this.opponent.setText("");
	}
}
