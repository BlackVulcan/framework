package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MenuView extends JMenuBar implements View {

    public static final int DISCONNECT_FROM_SERVER = 1;
    public static final int ENABLE_AI = 2;
    public static final int DISABLE_AI = 3;
    public static final int SERVER_CONNECTION_SHOW = 4;
    public static final int RETURN_TO_LOBBY = 5;
    public static final int TOGGLE_AI = 6;

    private static final long serialVersionUID = 1L;

    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();

    private JCheckBoxMenuItem playWithAIMenuItem;

    public MenuView() {
        JMenu start = new JMenu("Start");
        start.setMnemonic(KeyEvent.VK_S);
        JMenuItem menuItemConnect = new JMenuItem("Connect");
        menuItemConnect.setMnemonic(KeyEvent.VK_C);
        menuItemConnect.setToolTipText("Connect to a server");
        menuItemConnect.addActionListener(event -> {
            processEvent(new ActionEvent(this, SERVER_CONNECTION_SHOW, null));
        });
        start.add(menuItemConnect);

        JMenuItem menuItemdisConnect = new JMenuItem("Disconnect");
        menuItemdisConnect.setMnemonic(KeyEvent.VK_D);
        menuItemdisConnect.setToolTipText("Disconnect from the current server");
        menuItemdisConnect.addActionListener(event -> {
            processEvent(new ActionEvent(this, DISCONNECT_FROM_SERVER, null));
        });
        start.add(menuItemdisConnect);

        JMenuItem menuReturnToLobby = new JMenuItem("Return to lobby");
        menuReturnToLobby.setMnemonic(KeyEvent.VK_R);
        menuReturnToLobby.setToolTipText("Return to the lobby");
        menuReturnToLobby.addActionListener(event -> {
            processEvent(new ActionEvent(this, RETURN_TO_LOBBY, null));
        });
        start.add(menuReturnToLobby);

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_E);
        menuItemExit.setToolTipText("Exit application");
        menuItemExit.addActionListener(event -> System.exit(0));
        start.add(menuItemExit);

        JMenu intelligence = new JMenu("Intelligence");
        intelligence.setMnemonic(KeyEvent.VK_I);

        playWithAIMenuItem = new JCheckBoxMenuItem("Make AI play");
        playWithAIMenuItem.setMnemonic(KeyEvent.VK_E);
        playWithAIMenuItem.setToolTipText("When enabled, the AI will play moves for you");
        playWithAIMenuItem.addActionListener(event -> {
            processEvent(new ActionEvent(this, TOGGLE_AI, null));
        });
        intelligence.add(playWithAIMenuItem);

        this.add(start);
        this.add(intelligence);
    }

    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(actionListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }

    private void processEvent(ActionEvent e) {
        for (ActionListener l : actionListenerList)
            l.actionPerformed(e);
    }

    public void setPlayWithAI(boolean playWithAI) {
        playWithAIMenuItem.setState(playWithAI);
    }
}
