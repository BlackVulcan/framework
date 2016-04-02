package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

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
	public static final int CHALLENGE_RESPONSE = 4;
	public static final String CHALLENGE_ACCEPTED = "Accept";
	public static final String CHALLENGE_REJECTED = "Reject";
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
		challengeTable.setModel(new DefaultTableModel(
				new Object[][]{
				},
				new String[]{
						"ID", "Game", "Player", CHALLENGE_ACCEPTED, CHALLENGE_REJECTED
				}
				));

		challengeTable.getColumn(CHALLENGE_ACCEPTED).setCellRenderer(new ButtonRenderer());
		challengeTable.getColumn(CHALLENGE_ACCEPTED).setCellEditor(
				new ButtonEditor(new JCheckBox()));
		challengeTable.getColumn(CHALLENGE_REJECTED).setCellRenderer(new ButtonRenderer());
		challengeTable.getColumn(CHALLENGE_REJECTED).setCellEditor(
				new ButtonEditor(new JCheckBox()));
		challengePanel.add(new JScrollPane(challengeTable), BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		int sourceID = e.getID();
		if(object instanceof Model){
			Model model = (Model) object;
			if (sourceID == Model.NEW_CHALLENGE){
				int index = Integer.parseInt(e.getActionCommand());
				setChallenge(model.getChallenge(index));
			} else if(sourceID == Model.CANCEL_CHALLENGE){
				deleteChallenge(e.getActionCommand());
			}
		}
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
		model.addRow(new Object[]{challenge.get(Model.CHALLENGE_GAME_NUMBER), challenge.get(Model.CHALLENGE_GAME_TYPE), 
				challenge.get(Model.CHALLENGE_PLAYER), CHALLENGE_ACCEPTED, CHALLENGE_REJECTED, });
	}
	
	private void acceptChallenge(){
		processEvent(new ActionEvent(this, CHALLENGE_RESPONSE, CHALLENGE_ACCEPTED));
	}
	
	private void rejectChallenge(){
		if(challengeTable.getSelectedRow() != -1){
			DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
			deleteChallenge((String)challengeTableModel.getValueAt(challengeTable.getSelectedRow(), 0));
		}
	}

	public void deleteChallenge(String challengeNumber){
		DefaultTableModel tableModel = (DefaultTableModel) challengeTable.getModel();
		for(int i = tableModel.getRowCount() - 1; i >= 0; i--){
			if(challengeNumber.equals(tableModel.getValueAt(i,0))){
				tableModel.removeRow(i);
			}
		}
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

	public void stopAutomaticRefresh(){
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

	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	/**
	 * @version 1.0 11/09/98
	 */

	class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;

		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				int result = JOptionPane.showConfirmDialog(null, 
						label,null, JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION) {
					if(label.equals(CHALLENGE_ACCEPTED))
						acceptChallenge();
					else
						rejectChallenge();
				}
			}
			isPushed = false;
			return new String(label);
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}
}
