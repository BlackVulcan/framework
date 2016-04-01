package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LobbyView extends JPanel implements View {
	public static final int LOBBY_REFRESH = 1;
	public static final int PLAY_GAME = 2;
	private static final long serialVersionUID = 1L;
	JTable challengeTable;
	JList<String> playerList;
	JList<String> gameList;
	DefaultListModel<String> playerListModel, gameListModel;
	JPanel playPanel, lobbyPanel, playerPanel, challengePanel, gamePanel;
	JButton btnSpeel;
	private JPanel gamePlayerPanel;
	private boolean automaticRefresh = false;
	private ArrayList<ActionListener> actionListenerList = new ArrayList<>();

	@Override
	public void actionPerformed(ActionEvent e) {
		//todo: do something
	}

	public LobbyView() {
		playerListModel = new DefaultListModel<>();
		gameListModel = new DefaultListModel<>();
		playPanel = new JPanel();
		lobbyPanel = new JPanel();
		challengePanel = new JPanel();
		btnSpeel = new JButton("Speel");
		ActionEvent actionEvent = new ActionEvent(this, PLAY_GAME, null);
		btnSpeel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				processEvent(actionEvent);
			}
		});
		
		//challengeScrollPane = new JScrollPane();

		this.setLayout(new BorderLayout(0, 0));
		this.add(playPanel, BorderLayout.SOUTH);
		this.add(lobbyPanel, BorderLayout.CENTER);

		playPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		playPanel.add(btnSpeel);
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
						"Spel", "Speler", "Accepteren", "Weigeren"
				}
				));
		challengePanel.add(new JScrollPane(challengeTable), BorderLayout.CENTER);
	}

	public void setAvailableGames(List<String> games) {
		resetGameList();
		for (String game : games) {
			gameListModel.addElement(game);
		}
	}

	public void setAvailablePlayers(List<String> players) {
		resetPlayerList();
		for (String player : players) {
			playerListModel.addElement(player);
		}
	}

	private void setChallenge(String game, String player){
		DefaultTableModel model = (DefaultTableModel) challengeTable.getModel();
		
		//needs to be modified to accept or reject a challenge instead of "hoi"...
		model.addRow(new Object[]{game, player, "hoi", "hoi"});
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
		ActionEvent actionEvent = new ActionEvent(this, LOBBY_REFRESH, null);
		Runnable thread = new Runnable(){
			public void run(){
				while(automaticRefresh){
					try {
						processEvent(actionEvent);
						Thread.sleep(1000);
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
