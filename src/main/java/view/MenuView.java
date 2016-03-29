package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuView extends JMenuBar implements View{
	private static final long serialVersionUID = 1L;

	public MenuView(){
		JMenu file = new JMenu("Start");
        file.setMnemonic(KeyEvent.VK_S);
		JMenuItem mennuItemConnect = new JMenuItem("Connect");
		mennuItemConnect.setMnemonic(KeyEvent.VK_C);
		mennuItemConnect.setToolTipText("Connect to a server");
		mennuItemConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //do stuff -> of vang het af in controller door een event te lanceren
            }
        });
		file.add(mennuItemConnect);
		
		JMenuItem mennuItemdisConnect = new JMenuItem("Disconnect");
		mennuItemdisConnect.setMnemonic(KeyEvent.VK_D);
		mennuItemdisConnect.setToolTipText("Disconnect from the current server");
		mennuItemdisConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //do stuff -> of vang het af in controller door een event te lanceren
            }
        });
        file.add(mennuItemdisConnect);
        
        JMenuItem enuItemExit = new JMenuItem("Exit");
        enuItemExit.setMnemonic(KeyEvent.VK_E);
        enuItemExit.setToolTipText("Exit application");
        enuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        file.add(enuItemExit);
        
        this.add(file);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
