package controller;

/**
 * Created by Jules on 29-3-2016.
 */
public interface GameListener {
    public void match(String playerToMove, String gametype, String opponent);
    public void yourTurn(String turnmessage);
    public void move(String player, String move, String details);
    public void challenge(String challenger, String challengeNumber, String gametype);
    public void challengeCancelled(String challengeNumber);
    public void loss(String playerOneScore, String playerTwoScore, String comment);
    public void win(String playerOneScore, String playerTwoScore, String comment);
    public void draw(String playerOneScore, String playerTwoScore, String comment);
}
