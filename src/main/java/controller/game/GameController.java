package controller.game;

import model.Model;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;
import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;

import java.io.File;

/**
 * Created by Laurens on 30-3-2016.
 */
public class GameController implements GameListener,MoveListener {


    public static final String MODULE_PATH = "modules";
    private GameModuleLoader loader = new GameModuleLoader(new File(MODULE_PATH));
    private Model model;

    public GameController(Model model) {
        this.model = model;
    }

    @Override
    public void match(String playerToMove, String gametype, String opponent) {
        String playerOne = playerToMove;
        String playerTwo = playerOne.equals(opponent)?model.getClientName():opponent;
        AbstractGameModule module = loader.loadGameModule(gametype,playerOne,playerTwo);
        if(module instanceof ClientAbstractGameModule){
            model.setGameModule((ClientAbstractGameModule)module);
        }
    }

    @Override
    public void yourTurn(String turnmessage) {

    }

    @Override
    public void move(String player, String move, String details) {

    }

    @Override
    public void challenge(String challenger, String challengeNumber, String gametype) {

    }

    @Override
    public void challengeCancelled(String challengeNumber) {

    }

    @Override
    public void loss(String playerOneScore, String playerTwoScore, String comment) {

    }

    @Override
    public void win(String playerOneScore, String playerTwoScore, String comment) {

    }

    @Override
    public void draw(String playerOneScore, String playerTwoScore, String comment) {

    }

    @Override
    public void movePerformed(String s) {

    }
}
