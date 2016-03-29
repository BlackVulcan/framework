package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

public class LobbyView implements View{

	ArrayList<JButton> buttons;
	JTable table;
	JList playerList, gameList;
	JPanel container, playPanel, panel, playerPanel, challengePanel, gamePanel;
	JButton btnSpeel;
	JScrollPane gameScrollPane, playerScrollPane, challengeScrollPane;
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public LobbyView(){
	playerList = new JList();
	gameList = new JList();
	container = new JPanel();
	playPanel = new JPanel();
	panel = new JPanel();
	playerPanel = new JPanel();
	challengePanel = new JPanel();
	btnSpeel = new JButton("Speel");
	gamePanel = new JPanel();
	gameScrollPane = new JScrollPane();
	playerScrollPane = new JScrollPane();
	challengeScrollPane = new JScrollPane();
	
	container.setLayout(new BorderLayout(0, 0));
	container.add(playPanel, BorderLayout.SOUTH);
	container.add(panel, BorderLayout.CENTER);
	
	playPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	playPanel.add(btnSpeel);
	
	panel.setLayout(new BorderLayout(0, 0));
	panel.add(gamePanel, BorderLayout.WEST);
	panel.add(playerPanel, BorderLayout.EAST);
	panel.add(challengePanel, BorderLayout.SOUTH);

	gamePanel.add(gameScrollPane);
	gamePanel.add(gameList);

	playerPanel.add(playerScrollPane);
	playerPanel.add(playerList);
	
	challengePanel.add(challengeScrollPane);
	
	table = new JTable();
	table.setModel(new DefaultTableModel(
		new Object[][] {
		},
		new String[] {
			"Spel", "Accepteren", "Weigeren", "Speler"
		}
	));
	challengePanel.add(table);
	
	}
	
	public ArrayList<JButton> getButtons(){
		return buttons;
	}

}
