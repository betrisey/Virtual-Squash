package ninja.sam.virtualsquash;

import processing.core.PApplet;

public class Game {
    private Player[] players;
    private Player currentPlayer;
    private Ball ball;
    private int playersTurn;

    private int ballState;      // 0: Ne peut pas être frappée, 1: peut être frappée
    private int timeout = -1;   // -1: Pas de timer en cours

    PApplet parent;

    public Game(Player[] players, Ball ball, PApplet parent) {
        this.ball = ball;
        this.playersTurn = 0;
        this.players = players;

        this.parent = parent;
    }

    public void draw() {
        // Si la partie n'a pas commencé, on ne fait rien
        if (!getStatus())
            return;

        //déplace la ball , affiche la ball
        ball.move();
        ball.display();

        if (timeout > 0)
            parent.text(timeout, 500,500);

        if (ball.position.z > 400 && !ball.sens && ballState == 0) {
            // La balle peut �tre frapp�e
            ballState = 1;// Le joueur a 2 sec pour la frapper
            ball.color = currentPlayer.color;
            timeout = 100;
        } else if ((ball.position.z < 400 || ball.sens) && ballState == 1) {
            // La balle ne peut plus être frappée
            ballState = 0;
            timeout = -1;
            ball.color = 0;
            playerLost();
        }

        if (playerTouchingBall() && ballState == 1){
            ballState = 0;
            timeout = -1;
            ball.color = 0;
            //change le sens de la balle
            ball.sens = true;
            //fait rebondir
            ball.bounce(currentPlayer.getDirection());
            // Tour du prochain joueur
            nextTurn();
        }

        if (timeout == 0) {
            playerLost();
            timeout = 100;
        } else if (timeout > 0){
            timeout--;
        }
    }

    public boolean getStatus(){
        int counter = 0;
        for (int i = 0; i < players.length; i ++)
            if (players[i] != null)
                counter++;

        if (counter >= 1 && currentPlayer == null)
            currentPlayer = players[0];


        return counter >= 1;
    }

    private void nextTurn() {
        playersTurn++;
        if (playersTurn >= players.length)
            playersTurn = 0;

        if (players[playersTurn] != null)
            currentPlayer = players[playersTurn];
    }

    private void playerLost() {
        // Les autres joueurs marquent un point
        for (int i = 0; i<players.length; i++) {
            if (players[i] != null) {
                if (players[i] != currentPlayer)
                    players[i].score++;
            }
        }
        nextTurn();
    }

    private boolean playerTouchingBall() {
        return Math.abs(ball.position.x - currentPlayer.center.x) <= currentPlayer.width && Math.abs(ball.position.y - currentPlayer.center.y) <= currentPlayer.height;
    }
}
