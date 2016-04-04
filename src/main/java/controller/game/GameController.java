package controller.game;

import model.Model;
import model.ServerConnection;
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
    private ServerConnection serverConnection;
    private Model model;

    public GameController(Model model, ServerConnection serverConnection) {
        this.model = model;
        this.model.setGameModuleLoader(loader);
        this.serverConnection = serverConnection;
    }

    @Override
    public void match(String playerToMove, String gameType, String opponent) {
        if(playerToMove.equals(model.getClientName()))
        	model.setOpponent(opponent);
        else
        	model.setOpponent(playerToMove);
        
        model.setTurn(playerToMove);
        String playerOne = playerToMove;
        String playerTwo = playerOne.equals(opponent)?model.getClientName():opponent;
        
        AbstractGameModule module = loader.loadGameModule(gameType,playerOne,playerTwo);

        if(module instanceof ClientAbstractGameModule){
            ClientAbstractGameModule clientAbstractGameModule = (ClientAbstractGameModule)module;
            clientAbstractGameModule.start();
            clientAbstractGameModule.setClientBegins(!playerOne.equals(opponent));
            model.setGameModule(clientAbstractGameModule);
        }else{
            System.out.println("Was not an instance");
        }
        model.loadGame(playerToMove, gameType, opponent);
    }

    @Override
    public void yourTurn(String turnmessage) {
        model.setTurnMessage(turnmessage);
        model.setTurn(model.getClientName());
        if (model.getPlayWithAI()) {
            movePerformed(model.getGameModule().getAIMove());
        }
    }

    @Override
    public void move(String player, String move, String details) {
        model.getGameModule().doPlayerMove(player,move);
        model.setTurn(model.getGameModule().getPlayerToMove());
    }

    @Override
    public void challenge(String challenger, String challengeNumber, String gametype) {
        model.setNewChallenge(gametype, challenger, challengeNumber);
    }

    @Override
    public void challengeCancelled(String challengeNumber) {
        model.cancelChallenge(challengeNumber);
    }

    @Override
    public void loss(String playerOneScore, String playerTwoScore, String comment) {
    	this.model.setGameResult(Model.GAME_LOSS);
    }

    @Override
    public void win(String playerOneScore, String playerTwoScore, String comment) {
    	this.model.setGameResult(Model.GAME_WIN);
    }

    @Override
    public void draw(String playerOneScore, String playerTwoScore, String comment) {
    	this.model.setGameResult(Model.GAME_DRAW);
    }

    @Override
    public void movePerformed(String s) {
        if(serverConnection==null){
            System.err.println("Not connected to a server");
            return;
        }
//        model.getGameModule().doPlayerMove(model.getClientName(),s);
        if(model.getTurn())
            serverConnection.move(s);
    }

    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
}
