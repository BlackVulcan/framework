package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MenuView extends JMenuBar implements View {
    private static final long serialVersionUID = 1L;

    public MenuView() {
        JMenu file = new JMenu("Start");
        file.setMnemonic(KeyEvent.VK_S);
        JMenuItem mennuItemConnect = new JMenuItem("Connect");
        mennuItemConnect.setMnemonic(KeyEvent.VK_C);
        mennuItemConnect.setToolTipText("Connect to a server");
        mennuItemConnect.addActionListener(event -> {
            //todo: extend this to support other things.
            LoginBox loginBox = new LoginBox();
            loginBox.setVisible(true);
            //do stuff -> of vang het af in controller door een event te lanceren
        });
        file.add(mennuItemConnect);

        JMenuItem mennuItemdisConnect = new JMenuItem("Disconnect");
        mennuItemdisConnect.setMnemonic(KeyEvent.VK_D);
        mennuItemdisConnect.setToolTipText("Disconnect from the current server");
        mennuItemdisConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //todo: implement this.
                //do stuff -> of vang het af in controller door een event te lanceren
            }
        });
        file.add(mennuItemdisConnect);

        JMenuItem enuItemExit = new JMenuItem("Exit");
        enuItemExit.setMnemonic(KeyEvent.VK_E);
        enuItemExit.setToolTipText("Exit application");
        enuItemExit.addActionListener(event -> System.exit(0));
        file.add(enuItemExit);

        this.add(file);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }
}
