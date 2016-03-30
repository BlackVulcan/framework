package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LobbyView extends JPanel implements View {
	private static final long serialVersionUID = 1L;
	ArrayList<JButton> buttons;
	JTable table;
	JList<String> playerList;
	JList<String> gameList;
	DefaultListModel<String> playerListModel, gameListModel;
	JPanel playPanel, lobbyPanel, playerPanel, challengePanel, gamePanel;
	JButton btnSpeel;
	private JPanel gamePlayerPanel;

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

		table = new JTable();
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.setModel(new DefaultTableModel(
				new Object[][]{
				},
				new String[]{
						"Spel", "Speler", "Accepteren", "Weigeren"
				}
				));
		challengePanel.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public ArrayList<JButton> getButtons() {
		return buttons;
	}

	public void setAvailableGames(List<String> games) {
		for (String game : games) {
			gameListModel.addElement(game);
		}
	}

	public void setAvailablePlayers(List<String> players) {
		for (String player : players) {
			playerListModel.addElement(player);
		}
	}

	public void setChallenge(String game, String player){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[]{game, player, "hoi", "hoi"});
	}
}
