package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Model;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LobbyView extends JPanel implements View {
	public static final int LOBBY_REFRESH = 1;
	public static final int PLAY_GAME = 2;
	public static final int CHALLENGE_PLAYER = 3;
	private static final long serialVersionUID = 1L;
	private JTable challengeTable;
	private JList<String> playerList;
	private JList<String> gameList;
	private DefaultListModel<String> playerListModel, gameListModel;
	private JPanel playPanel, lobbyPanel, playerPanel, challengePanel, gamePanel;
	private JButton play, challenge;
	private JPanel gamePlayerPanel;
	private boolean automaticRefresh = false;
	private ArrayList<ActionListener> actionListenerList = new ArrayList<>();

	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		int sourceID = e.getID();
		if(object instanceof Model){
			Model model = (Model) object;
			if (sourceID == Model.NEW_CHALLENGE){
				int index = Integer.parseInt(e.getActionCommand());
				setChallenge(model.getChallenge(index));
			}
		}
	}

	public LobbyView() {
		playerListModel = new DefaultListModel<>();
		gameListModel = new DefaultListModel<>();
		playPanel = new JPanel();
		lobbyPanel = new JPanel();
		challengePanel = new JPanel();

		this.setLayout(new BorderLayout(0, 0));
		this.add(playPanel, BorderLayout.SOUTH);
		this.add(lobbyPanel, BorderLayout.CENTER);

		playPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));		

		play = new JButton("Play");
		ActionEvent playGame = new ActionEvent(this, PLAY_GAME, null);
		play.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				processEvent(playGame);
			}
		});
		playPanel.add(play);
		
		challenge = new JButton("Challenge");
		ActionEvent challengePlayer = new ActionEvent(this, CHALLENGE_PLAYER, null);
		challenge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				processEvent(challengePlayer);
			}
		});
		playPanel.add(challenge);
		lobbyPanel.setLayout(new GridLayout(0, 1, 0, 0));

		gamePlayerPanel = new JPanel();
		lobbyPanel.add(gamePlayerPanel);
		gamePlayerPanel.setLayout(new GridLayout(1, 0, 0, 0));
		gameList = new JList<>(gameListModel);
		gamePanel = new JPanel();
		gamePanel.setLayout(new BorderLayout(0, 0));
		gamePanel.add(new JScrollPane(gameList), BorderLayout.CENTER);
		gamePlayerPanel.add(gamePanel);

		playerList = new JList<>(playerListModel);
		playerPanel = new JPanel();
		gamePlayerPanel.add(playerPanel);
		playerPanel.setLayout(new BorderLayout(0, 0));
		playerPanel.add(new JScrollPane(playerList));
		lobbyPanel.add(challengePanel);
		challengePanel.setLayout(new BorderLayout(0, 0));

		challengeTable = new JTable();
		challengeTable.setPreferredScrollableViewportSize(challengeTable.getPreferredSize());
		challengeTable.setFillsViewportHeight(true);
		challengeTable.setModel(new DefaultTableModel(
				new Object[][]{
				},
				new String[]{
						"Game", "Player", "Accept", "Deny"
				}
				));
		challengePanel.add(new JScrollPane(challengeTable), BorderLayout.CENTER);
	}

	public String getSelectedGame(){
		return gameListModel.getElementAt(gameList.getSelectedIndex());
	}

	public void setAvailableGames(List<String> games) {
		resetGameList();
		for (String game : games) {
			gameListModel.addElement(game);
		}
	}
	
	public String getSelectedPlayer(){
		return playerListModel.getElementAt(playerList.getSelectedIndex());
	}

	public void setAvailablePlayers(List<String> players, String clientName) {
		for(int i = playerListModel.size() - 1; i >= 0; i--) {
			if(!players.contains(playerListModel.get(i))){
				playerListModel.remove(i);
			}
		}
		
		for(int i = 0; i < players.size(); i++){
			if(!playerListModel.contains(players.get(i)) && !players.get(i).equals(clientName)){
				playerListModel.addElement(players.get(i));
			}
		}
	}

	private void setChallenge(HashMap<String, String> challenge){
		DefaultTableModel model = (DefaultTableModel) challengeTable.getModel();
		
		//needs to be modified to accept or reject a challenge
		model.addRow(new Object[]{challenge.get(Model.CHALLENGE_GAME_TYPE), 
				challenge.get(Model.CHALLENGE_PLAYER), "", ""});
	}
	
	private void resetChallenge(){
		DefaultTableModel tableModel = (DefaultTableModel) challengeTable.getModel();
		for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
	}
	
	private void resetPlayerList(){
		DefaultListModel<String> playerListModel = (DefaultListModel<String>) playerList.getModel();
		for (int i = playerListModel.size() - 1; i >= 0; i--) {
			playerListModel.remove(i);
		}
	}
	
	private void resetGameList(){
		DefaultListModel<String> gameListModel = (DefaultListModel<String>) gameList.getModel();
		for (int i = gameListModel.size() - 1; i >= 0; i--) {
			gameListModel.remove(i);
		}
	}
	
	public void reset(){
		resetChallenge();
		resetPlayerList();
		resetGameList();
	}
	
	public void stopAutomatichRefresh(){
		automaticRefresh = false;
	}
	
	public void automaticRefresh(){
		automaticRefresh = true;
		ActionEvent refreshLobby = new ActionEvent(this, LOBBY_REFRESH, null);
		Runnable thread = new Runnable(){
			public void run(){
				while(automaticRefresh){
					try {
						processEvent(refreshLobby);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}        
		};
		new Thread(thread).start();
	}
	
	private void processEvent(ActionEvent e) {
        for (ActionListener l : actionListenerList)
            l.actionPerformed(e);
    }
	
	public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(actionListener);
    }
}
