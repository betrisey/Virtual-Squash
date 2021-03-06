/*
Virtual Squash 2 - 2015 - Samuel Bétrisey
Classe Game
 - Gère les tours des joueurs
 - Calcule des scores des joueurs
 - Vérifie si le joueur frappe la balle
 - Désigne le vainqueur et si la partie est finie
 - Choisi le bon mode de jeu si le tableau players contient 1 ou 2 joueurs
*/
package ninja.sam.virtualsquash;

public class Game {

    private Player[] players;
    private Player currentPlayer;
    private Ball ball;

    private int playersTurn;    // Correspond à l'index d'un joueur du tableau "players"
    private int ballState;      // 0: Ne peut pas être frappée, 1: peut être frappée
    private int timeout = -1;   // -1: Pas de timer en cours

    public int maxScore = 0;    // Pour le mode de jeu solo

    private static int TIMEOUT = 50; // Temps que le joueur a pour frapper la balle lorsqu'elle arrive vers lui

    public Game(Player[] players, Ball ball) {
        this.ball = ball;
        this.playersTurn = 0;
        this.players = players;

        ball.ballReset();
    }

    public void draw() {
        // Si la partie n'a pas commencé, on ne fait rien
        if (!getStatus())
            return;

        // déplace la ball , affiche la ball
        ball.move();
        ball.display();

        if (countPlayers() >= 2) {
            // S'il y a 2 joueurs ou plus, chacun joue à son tour
            // celui qui fait une faute donne 1 point à ses adversaires
            maxScore = 0; // Utilisé seulement pour 2 joueurs
            if (ball.position.z > 400 && !ball.sens && ballState == 0) {
                // La balle peut être frappée
                ballState = 1;// Le joueur a 2 sec pour la frapper
                ball.color = currentPlayer.color;
                timeout = TIMEOUT;
            } else if ((ball.position.z < 400 || ball.sens) && ballState == 1) {
                // La balle ne peut plus être frappée
                ballState = 0;
                timeout = -1;
                ball.color = 0;
                playerLost();
            }

            if (playerTouchingBall() && ballState == 1) {
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
                timeout = TIMEOUT;
            } else if (timeout > 0) {
                timeout--;
            }
        } else if (countPlayers() == 1) {
            // 1 Joueur
            currentPlayer = players[0];
            if (ball.position.z > 400 && !ball.sens && ballState == 0) {
                // La balle peut être frappée
                ballState = 1;// Le joueur a 2 sec pour la frapper
                ball.color = currentPlayer.color;
                timeout = TIMEOUT;
            } else if ((ball.position.z < 400 || ball.sens) && ballState == 1) {
                // La balle ne peut plus être frappée
                ballState = 0;
                timeout = -1;
                ball.color = 0;
                currentPlayer.score = 0;
                ball.ballReset();
            }

            if (playerTouchingBall() && ballState == 1) {
                ballState = 0;
                timeout = -1;
                ball.color = 0;
                //change le sens de la balle
                ball.sens = true;
                //fait rebondir
                ball.bounce(currentPlayer.getDirection());

                currentPlayer.score++;

                // Tous les 10 points, accélère la balle
                if (currentPlayer.score % 10 == 0)
                    ball.accelerate(1.2f);
                if (currentPlayer.score > maxScore)
                    maxScore = currentPlayer.score;
            }

            if (timeout == 0) {
                currentPlayer.score = 0;
                timeout = TIMEOUT;
                ball.ballReset();
            } else if (timeout > 0) {
                timeout--;
            }
        }
    }

    public boolean getStatus(){
        // On peut commencer lorsqu'il y a un joueur

        int counter = countPlayers();

        // Si le joueur actuel n'est pas défini, c'est le premier joueur
        if (counter >= 1 && currentPlayer == null) {
            currentPlayer = players[0];
        }

        return counter >= 1;
    }

    private void nextTurn() {
        // C'est au prochain joueur de frapper la balle

        playersTurn++;
        if (playersTurn >= players.length)
            playersTurn = 0;

        if (players[playersTurn] != null) {
            currentPlayer = players[playersTurn];
        }
    }

    private void playerLost() {
        // Les autres joueurs marquent un point
        for (Player player : players) {
            if (player != null) {
                if (player != currentPlayer)
                    player.score++;
            }
        }
        // Tour du prochain joueur
        nextTurn();
        ball.color = currentPlayer.color;
    }

    private boolean playerTouchingBall() {
        // Vérifie si le joueur actuel touche la balle
        return Math.abs(ball.position.x - currentPlayer.center.x) <= currentPlayer.width && Math.abs(ball.position.y - currentPlayer.center.y) <= currentPlayer.height;
    }

    private int countPlayers() {
        // Compte le nombre de joueurs

        int counter = 0;
        for (Player player : players)
            if (player != null)
                counter++;
        return counter;
    }

    public int checkWinner() {
        // S'il y a 2 joueurs, le gagnant et plus premier à 21 points et 2 points d'écart
        // Retourne -1 s'il n'y a pas de gagnant sinon l'index du gagnant

        int gagnant = -1;

        if (countPlayers() == 2) {
            if (players[0] != null && players[1] != null && (players[0].score >= 21 || players[1].score >= 21) && Math.abs(players[0].score - players[1].score) >= 2) {
                // un des deux joueurs a gagnant
                if (players[0].score > players[1].score)
                    gagnant = 0;
                else
                    gagnant = 1;
            }
        }

        return gagnant;
    }
}
