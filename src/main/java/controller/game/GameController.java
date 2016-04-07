package controller.game;

import model.Model;
import model.ServerConnection;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;
import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class GameController implements GameListener, MoveListener {
    public static final String MODULE_PATH = "modules";
	private static final Logger logger = LogManager.getLogger(GameController.class);
    private ServerConnection serverConnection;
    private Model model;
	private GameModuleLoader loader;

    public GameController(Model model, ServerConnection serverConnection) {
        this.model = model;
        loader = new GameModuleLoader(new File(MODULE_PATH), model);
        this.model.setGameModuleLoader(loader);
        this.serverConnection = serverConnection;
    }

    @Override
    public void match(String playerToMove, String gameType, String opponent) {
	    if (playerToMove.equals(model.getClientName())) {
		    model.setOpponent(opponent);
	    } else {
		    model.setOpponent(playerToMove);
	    }

        model.setTurn(playerToMove);
	    @SuppressWarnings("UnnecessaryLocalVariable") String playerOne = playerToMove;
	    String playerTwo = playerOne.equals(opponent) ? model.getClientName() : opponent;

	    AbstractGameModule module = loader.loadGameModule(gameType, playerOne, playerTwo);

	    logger.trace("Starting {} match. Player one: {}. Player two: {}", gameType, playerOne, playerTwo);
	    if (module instanceof ClientAbstractGameModule) {
		    ClientAbstractGameModule clientAbstractGameModule = (ClientAbstractGameModule) module;
            clientAbstractGameModule.setClientBegins(!playerOne.equals(opponent));
            clientAbstractGameModule.setClientPlayPiece(model.getChosenGameSides(gameType));
		    clientAbstractGameModule.start();
            model.setGameModule(clientAbstractGameModule);
	    } else {
		    logger.fatal("{} was not an instance of ClientAbstractGameModule", module.getClass().getName());
	    }
        model.loadGame(playerToMove, gameType, opponent);
    }

    @Override
    public void yourTurn(String turnMessage) {
	    model.setTurnMessage(turnMessage);
	    if (model.getPlayWithAI()) {
            movePerformed(model.getGameModule().getAIMove());
        }
    }

    @Override
    public void move(String player, String move, String details) {
	    model.getGameModule().doPlayerMove(player, move);
        try {
            model.setTurn(model.getGameModule().getPlayerToMove());
        } catch (IllegalStateException e) {
            
        }
    }

    @Override
    public void challenge(String challenger, String challengeNumber, String gameType) {
	    model.setNewChallenge(gameType, challenger, challengeNumber);
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
	    if (serverConnection == null) {
		    System.err.println("Not connected to a server");
            return;
        }
//        model.getGameModule().doPlayerMove(model.getClientName(),s);
	    if (model.getTurn())
		    new Thread(() -> serverConnection.move(s)).start();
    }

    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
}
