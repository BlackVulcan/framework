package controller.game;

/**
 * This Interface represents messages that the server can send. By implementing it, you can react to server messages
 * Created by Jules on 29-3-2016.
 */
public interface GameListener {
    /**
     * Invoked when a new match begins
     *
     * @param playerToMove The player who wil start the match
     * @param gameType     The name of the game which will be played
     * @param opponent     The name of the opponent
     */
    void match(String playerToMove, String gameType, String opponent);

    /**
     * Invoked when it is the client's turn in a game
     *
     * @param turnMessage A message giving more information about the turn.
     */
    void yourTurn(String turnMessage);

    /**
     * Invoked when a move is done
     *
     * @param player  The player who has performed the move
     * @param move    The move that the player has performed
     * @param details Details about the move
     */
    void move(String player, String move, String details);

    /**
     * Invoked when the client is being challenged
     *
     * @param challenger      The person who challenged the client
     * @param challengeNumber The number of the challenge (used to accept the challenge)
     * @param gameType        The type of game being played.
     */
    void challenge(String challenger, String challengeNumber, String gameType, String challengeTurnTime);

    /**
     * Invoked when a challenge is cancelled
     *
     * @param challengeNumber The number of the challenge being cancelled
     */
    void challengeCancelled(String challengeNumber);

    /**
     * Invoked when the client has lost a game
     *
     * @param playerOneScore The end score of player one
     * @param playerTwoScore The end score of player two
     * @param comment        A comment given by the server
     */
    void loss(String playerOneScore, String playerTwoScore, String comment);

    /**
     * Invoked when the client has won a game
     *
     * @param playerOneScore The end score of player one
     * @param playerTwoScore The end score of player two
     * @param comment        A comment given by the server
     */
    void win(String playerOneScore, String playerTwoScore, String comment);

    /**
     * Invoked when the client has played a game which ended in draw.
     *
     * @param playerOneScore The end score of player one
     * @param playerTwoScore The end score of player two
     * @param comment        A comment given by the server
     */
    void draw(String playerOneScore, String playerTwoScore, String comment);
}
