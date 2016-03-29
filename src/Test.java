import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Jules on 29-3-2016.
 */
public class Test implements GameListener {
    @Override
    public void match(String playerToMove, String gametype, String opponent) {
        System.out.println("Test.match");
        System.out.printf("playerToMove = %s gametype = %s opponent = %s\n",playerToMove,gametype,opponent);
    }

    @Override
    public void yourTurn(String turnmessage) {
        System.out.println("yourturn");
        System.out.printf("TurnMessage = %s\n",turnmessage);
    }

    @Override
    public void move(String player, String move, String details) {
        System.out.println("Test.move");
        System.out.printf("player = %s move = %s details = %s \n",player,move,details);
    }

    @Override
    public void challenge(String challenger, String challengeNumber, String gametype) {
        System.out.println("Test.challenge");
        System.out.printf("challenger = %s challengeNumber = %s gametype = %s\n",challenger,challengeNumber,gametype);
    }

    @Override
    public void loss(String playerOneScore, String playerTwoScore, String comment) {
        System.out.println("Test.loss");
        System.out.printf("playerOneScore = %s playerTwoScore = %s comment = %s \n",playerOneScore,playerTwoScore,comment);
    }

    @Override
    public void win(String playerOneScore, String playerTwoScore, String comment) {
        System.out.println("Test.win");
        System.out.printf("playerOneScore = %s playerTwoScore = %s comment = %s \n",playerOneScore,playerTwoScore,comment);
    }

    @Override
    public void draw(String playerOneScore, String playerTwoScore, String comment) {
        System.out.println("Test.draw");
        System.out.printf("playerOneScore = %s playerTwoScore = %s comment = %s \n",playerOneScore,playerTwoScore,comment);
    }

    @Override
    public void challengeCancelled(String challengeNumber) {
        System.out.println("Test.challengeCancelled");
        System.out.println("challengenumber = " + challengeNumber);
    }

    public static void main(String[] args) {
        try {
            ServerConnection connection = new ServerConnection("145.37.66.153",7789);
            connection.addGameListener(new Test());
            String line;
            Scanner scanner = new Scanner(System.in);
            connection.login("jules");
            connection.challenge("laurens", "Guess Game");
            for (String arg : connection.getPlayerlist()) {
                System.out.println(arg);
            }
            while(!(line = scanner.nextLine()).equals("logout")){
                connection.write(line);
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
