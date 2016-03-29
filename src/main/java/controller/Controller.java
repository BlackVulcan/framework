package controller;

import model.Model;
import model.ServerConnection;
import view.ContainerView;
import view.MenuView;
import view.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Controller implements ActionListener {
    private ServerConnection serverConnection;
    ContainerView containerView;
    MenuView menuView;

    public Controller(Model model) {
        this.containerView = new ContainerView();
        this.menuView = new MenuView();

        containerView.setJMenuBar(menuView);

        //add actionListeners to control buttons
        for (JButton button : containerView.getButtons()) {
            button.addActionListener(this);
        }

        this.containerView.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof Model) {
            //do stuff
        } else if (source instanceof View) {
            //do stuff
        }
    }

    public boolean connect(String hostname, int port) {
        try {
            serverConnection = new ServerConnection(hostname, port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username) {
        return serverConnection.login(username);
    }

    public boolean logout() {
        //todo: implement method.
        throw new RuntimeException("Not implemented");
//        return serverConnection.logout();
    }

    public boolean subscribe(String gameName) {
        return serverConnection.subscribe(gameName);
    }

    public void challenge(String player, String gameMode) {
        serverConnection.challenge(player, gameMode);
    }

    public void acceptChallenge(String challengeId) {
        serverConnection.acceptChallenge(challengeId);
    }

    public void acceptMatch() {
        //todo: implement method.
        throw new RuntimeException("Not implemented");
    }
}
