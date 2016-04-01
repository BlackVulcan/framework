package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MenuView extends JMenuBar implements View {
    public static final int DiSCONNECT_FROM_SERVER = 1;
    public static final int PLAY_WITH_AI = 2;
    public static final int SERVER_CONNECTION_SHOW = 3;
    private static final long serialVersionUID = 1L;
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();

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
            processEvent(new ActionEvent(this, DiSCONNECT_FROM_SERVER, null));
        });
        start.add(menuItemdisConnect);

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_E);
        menuItemExit.setToolTipText("Exit application");
        menuItemExit.addActionListener(event -> System.exit(0));
        start.add(menuItemExit);
        
        JMenu intelligence = new JMenu("Intelligence");
        intelligence.setMnemonic(KeyEvent.VK_I);
        JMenuItem menuItemPlayWithAI = new JMenuItem("Let AI play");
        menuItemPlayWithAI.setMnemonic(KeyEvent.VK_L);
        menuItemPlayWithAI.setToolTipText("Activate AI to play for you");
        menuItemPlayWithAI.addActionListener(event -> {
            //if gamemodule is loaded:
            if(true)
            	processEvent(new ActionEvent(this, PLAY_WITH_AI, null));
        });
        intelligence.add(menuItemPlayWithAI);

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
}
