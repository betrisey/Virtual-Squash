package ninja.sam.virtualsquash;

public class Game {
    public Player[] players;
    public Ball ball;
    public int playersTurn;
    private boolean ballCanBeHit;
    private boolean ballHit;    // La balle a été frappée

    public Game(Player[] players, Ball ball) {
        this.ball = ball;
        this.playersTurn = 0;
        this.players = players;
    }

    public void updateScore() {
        if (ball.position.z > 400 && !ball.sens) {
            // La balle peut être frappée
            ballCanBeHit = true;
            ballHit = false;
        } else if (ballCanBeHit && !ballHit && ball.position.z < 400) {
            // La balle pouvait être frappée mais le
            // joueur ne l'a pas fait
            playerLost();

            ballCanBeHit = false;
        }

        if (playerTouchingBall()){
            ballHit = true;
            ballCanBeHit = false;

            //change le sens de la balle
            ball.sens = true;
            //reset le timer de gameover
            ball.gameOver = 0;
            //fait rebondir
            ball.bounce(players[playersTurn].getDirection());
        }
    }

    private void nextTurn() {
        playersTurn++;
        if (playersTurn >= players.length)
            playersTurn = 0;
    }

    private void playerLost() {
        // Les autres joueurs marquent un point
        for (int i = 0; i<players.length; i++) {
            if (i != playersTurn)
                players[i].score++;
        }
        nextTurn();
    }

    private boolean playerTouchingBall() {
        return Math.abs(ball.position.x - players[playersTurn].center.x) <= players[playersTurn].width && Math.abs(ball.position.y - players[playersTurn].center.y) <= players[playersTurn].height;
    }
}
