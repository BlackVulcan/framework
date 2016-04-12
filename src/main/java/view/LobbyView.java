package view;

import model.Model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class LobbyView extends JPanel implements View {
	public static final int LOBBY_REFRESH = 1;
	public static final int PLAY_GAME = 2;
	public static final int CHALLENGE_PLAYER = 3;
	public static final int CHALLENGE_ACCEPTED = 4;
	public static final String CHALLENGE_ACCEPT = "Accept";
	public static final String CHALLENGE_REJECT = "Reject";
	private static final long serialVersionUID = 1L;
	private JTable challengeTable;
	private JList<String> playerList;
	private JList<String> gameList;
	private DefaultListModel<String> playerListModel, gameListModel;
	private JPanel playPanel, lobbyPanel, playerPanel, challengePanel, gamePanel;
	private JButton subscribe, challenge;
	private JPanel gamePlayerPanel;
	private boolean automaticRefresh = false;
	private ArrayList<ActionListener> actionListenerList = new ArrayList<>();
	DefaultTableModel challengeTableModel;

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

		subscribe = new JButton("Subscribe");
		ActionEvent playGame = new ActionEvent(this, PLAY_GAME, null);
		subscribe.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				processEvent(playGame);
			}
		});
		playPanel.add(subscribe);

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
		challengeTableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Game", "Player", "Turn Time", CHALLENGE_ACCEPT, CHALLENGE_REJECT });
		challengeTable.setModel(challengeTableModel);
		
		challengeTable.setAutoCreateColumnsFromModel(false);
		challengeTable.getTableHeader().setReorderingAllowed(false);

	    Vector data = challengeTableModel.getDataVector();
	    Collections.sort(data, new ColumnSorter(1));
	    challengeTableModel.fireTableStructureChanged();
		
		Action acceptChallenge = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) e.getSource();
				int modelRow = Integer.valueOf(e.getActionCommand());
				acceptChallenge((String) table.getValueAt(modelRow, 0));
			}
		};

		Action rejectChallenge = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, CHALLENGE_REJECT + " challenge?", null,
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					JTable table = (JTable) e.getSource();
					int modelRow = Integer.valueOf(e.getActionCommand());
					((DefaultTableModel) table.getModel()).removeRow(modelRow);
				}
			}
		};

		new ButtonColumn(challengeTable, acceptChallenge, 4);
		new ButtonColumn(challengeTable, rejectChallenge, 5);

		challengePanel.add(new JScrollPane(challengeTable), BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		int sourceID = e.getID();
		if (object instanceof Model) {
			Model model = (Model) object;
			if (sourceID == Model.NEW_CHALLENGE) {
				int index = Integer.parseInt(e.getActionCommand());
				setChallenge(model.getChallenge(index));
			} else if (sourceID == Model.CANCEL_CHALLENGE) {
				deleteChallenge(e.getActionCommand());
			}
		}
	}

	public String getSelectedGame() {
		int gameIndex = gameList.getSelectedIndex();
		if (gameIndex >= 0)
			return gameListModel.getElementAt(gameIndex);
		return null;
	}

	public void setAvailableGames(List<String> games) {
		resetGameList();
		for (String game : games) {
			gameListModel.addElement(game);
		}
	}

	public String getSelectedPlayer() {
		int playerIndex = playerList.getSelectedIndex();
		if (playerIndex >= 0)
			return playerListModel.getElementAt(playerIndex);
		return null;
	}

	public void setAvailablePlayers(List<String> players, String clientName) {
		for (int i = playerListModel.size() - 1; i >= 0; i--) {
			if (!players.contains(playerListModel.get(i))) {
				playerListModel.remove(i);
			}
		}

		for (int i = 0; i < players.size(); i++) {
			if (!playerListModel.contains(players.get(i)) && !players.get(i).equals(clientName)) {
				playerListModel.addElement(players.get(i));
			}
		}
	}

	private void setChallenge(HashMap<String, String> challenge) {
		DefaultTableModel model = (DefaultTableModel) challengeTable.getModel();

		// needs to be modified to accept or reject a challenge
		model.addRow(
				new Object[] { challenge.get(Model.CHALLENGE_GAME_NUMBER), challenge.get(Model.CHALLENGE_GAME_TYPE),
						challenge.get(Model.CHALLENGE_PLAYER), challenge.get(Model.CHALLENGE_TURN_TIME), CHALLENGE_ACCEPT, CHALLENGE_REJECT, });
	}

	private void acceptChallenge(String challengeNumber) {
		// ((DefaultTableModel) challengeTable.getModel()).removeRow(modelRow);
		processEvent(new ActionEvent(this, CHALLENGE_ACCEPTED, challengeNumber));
	}

	public void deleteChallenge(String challengeNumber) {
		DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
		for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
			if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
				challengeTableModel.removeRow(i);
			}
		}
	}

	private void resetChallenge() {
		DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
		for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
			challengeTableModel.removeRow(i);
		}
	}

	public String getPlayerFromChallenge(String challengeNumber) {
		DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
		for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
			if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
				return (String) challengeTableModel.getValueAt(i, 2);
			}
		}
		return null;
	}

	public String getGameTypeFromChallenge(String challengeNumber) {
		DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
		for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
			if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
				return (String) challengeTableModel.getValueAt(i, 1);
			}
		}
		return null;
	}

	public String getTurnTimeFromChallenge(String challengeNumber) {
		DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
		for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
			if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
				return (String) challengeTableModel.getValueAt(i, 3);
			}
		}
		return null;
	}

	private void resetPlayerList() {
		DefaultListModel<String> playerListModel = (DefaultListModel<String>) playerList.getModel();
		for (int i = playerListModel.size() - 1; i >= 0; i--) {
			playerListModel.remove(i);
		}
	}

	private void resetGameList() {
		DefaultListModel<String> gameListModel = (DefaultListModel<String>) gameList.getModel();
		for (int i = gameListModel.size() - 1; i >= 0; i--) {
			gameListModel.remove(i);
		}
	}

	public void reset() {
		resetChallenge();
		resetPlayerList();
		resetGameList();
	}

	public void stopAutomaticRefresh() {
		automaticRefresh = false;
	}

	public void automaticRefresh() {
		automaticRefresh = true;
		ActionEvent refreshLobby = new ActionEvent(this, LOBBY_REFRESH, null);
		Runnable thread = new Runnable() {
			public void run() {
				while (automaticRefresh) {
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

class ColumnSorter implements Comparator {
	int colIndex;

	ColumnSorter(int colIndex) {
		this.colIndex = colIndex;
	}

	public int compare(Object a, Object b) {
		Vector v1 = (Vector) a;
		Vector v2 = (Vector) b;
		Object o1 = v1.get(colIndex);
		Object o2 = v2.get(colIndex);

		if (o1 instanceof String && ((String) o1).length() == 0) {
			o1 = null;
		}
		if (o2 instanceof String && ((String) o2).length() == 0) {
			o2 = null;
		}

		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) {
			return 1;
		} else if (o2 == null) {
			return -1;
		} else if (o1 instanceof Comparable) {

			return ((Comparable) o1).compareTo(o2);
		} else {

			return o1.toString().compareTo(o2.toString());
		}
	}
}
