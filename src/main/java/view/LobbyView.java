package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

public class LobbyView extends JPanel implements View{

	ArrayList<JButton> buttons;
	JTable table;
	JList playerList, gameList;
	DefaultListModel<String> playerListModel, gameListModel;
	JPanel playPanel, lobbyPanel, playerPanel, challengePanel, gamePanel;
	JButton btnSpeel;
	private JPanel gamePlayerPanel;



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public LobbyView(){
		playerListModel = new DefaultListModel<>();
		gameListModel  = new DefaultListModel<>();
		playPanel = new JPanel();
		lobbyPanel = new JPanel();
		challengePanel = new JPanel();
		btnSpeel = new JButton("Speel");
		//challengeScrollPane = new JScrollPane();

		this.setLayout(new BorderLayout(0, 0));
		this.add(playPanel, BorderLayout.SOUTH);
		this.add(lobbyPanel, BorderLayout.CENTER);

		playPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		playPanel.add(btnSpeel);

		lobbyPanel.setLayout(new BorderLayout(0, 0));
		lobbyPanel.add(challengePanel, BorderLayout.SOUTH);

		

		table = new JTable();
		table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"Spel", "Accepteren", "Weigeren", "Speler"
				}
				));
		challengePanel.add(new JScrollPane(table));

		gamePlayerPanel = new JPanel();
		lobbyPanel.add(gamePlayerPanel, BorderLayout.CENTER);
		gamePlayerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		gameList = new JList(gameListModel);
		gamePanel = new JPanel();
		gamePanel.add(new JScrollPane(gameList));
		gamePlayerPanel.add(gamePanel);
		
		playerList = new JList(playerListModel);
		playerPanel = new JPanel();
		gamePlayerPanel.add(playerPanel);
		playerPanel.add(new JScrollPane(playerList));

	}

	public ArrayList<JButton> getButtons(){
		return buttons;
	}

	public void setAvailableGames(List<String> games){
		for(String game : games){
			gameListModel.addElement(game);
		}
	}

	public void setAvailablePlayers(List<String> players){
		for(String player : players){
			playerListModel.addElement(player);
		}
	}
}
