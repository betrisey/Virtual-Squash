/*
Virtual Squash 2 - 2015 - Samuel Bétrisey
Classe Main
 - récupère la position du joueur depuis la Kinect
 - envoie ces données à la classe Player
 - instancie la classe Ball et Game
 - vérifie et affiche les scores, le vainqueur en
   appelant des méthodes des objets Player et Game
 - affiche les scores, bouton recommencer
*/

package ninja.sam.virtualsquash;

import KinectPV2.KinectPV2;
import processing.core.*;
import KinectPV2.*;

import java.util.ArrayList;

public class Main extends PApplet {
    private static int LONGUEUR_ECRAN = 1680;
    private static int LARGEUR_ECRAN = 945;
    private static int NOMBRE_JOUEURS = 2;
    // Pour la convertion des distances de mètres en pixel
    // Varie selon la taille de l'écran et la distance du joueur
    private static final float FACTEURHORIZONTAL = 1.2f;
    private static final float FACTEURVERTICAL = 0.5f;

    private KinectPV2 kinect;

    private Player[] players;
    private Ball ball;
    private Game game;

    private PImage backgroundImage = new PImage();

    private int restartButtonPressed = 0;

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "ninja.sam.virtualsquash.Main" });
    }

    public void settings() {
        size(LONGUEUR_ECRAN, LARGEUR_ECRAN, P3D);
    }

    public void setup()
    {
        // Initialise la Kinect
        kinect = new KinectPV2(this);
        kinect.enableColorImg(true);
        kinect.enableSkeleton3DMap(true);
        kinect.init();

        // Initialise le jeu
        ball = new Ball(this, LARGEUR_ECRAN, LONGUEUR_ECRAN);
        players = new Player[NOMBRE_JOUEURS];
        game = new Game(players, ball);

        // Améliore la fluidité
        smooth();
        frameRate(30);

        // Charge la police d'écriture
        PFont font = loadFont("assets/Lato-Semibold-75.vlw");
        textFont(font, 25);

        // Charge et redimensionne l'image de fond
        backgroundImage = loadImage("assets/SalleSquash.jpg");
        backgroundImage.resize(LONGUEUR_ECRAN, LARGEUR_ECRAN);
    }

    public void draw()
    {
        // Méthode appelée à chaque calcul d'une nouvelle image (environ 30 fois par seconde)

        // Affiche la salle de squash en arrière-plan
        background(backgroundImage);

        // Affiche l'image de la kinect en transparence
        tint(255, 75);
        image(kinect.getColorImage(), 0, 0, LONGUEUR_ECRAN, LARGEUR_ECRAN);
        tint(255, 255);

        textSize(25);

        // Affichage du nombre d'images par seconde et le nombre de joueurs détectés
        fill(0, 0, 0, 255);
        text("FPS : " + Math.round(frameRate), 300, 30);
        text("Joueurs detectes :" + kinect.getSkeleton3d().size(), 420, 30);

        // Affiche le score de chaque joueur
        for(int i=0; i<NOMBRE_JOUEURS; i++) {
            if (players[i] != null) {
                // Le score a la même couleur que la raquette du joueur
                fill(players[i].color);
                text("Score joueur " + (i + 1) + " : " + players[i].score, 20, i * 45 + 30);
            }
        }

        // En solo, affichage du meilleur score
        if (game.maxScore > 0) {
            fill(players[0].color);
            text("Score max : " + game.maxScore, 20, 60);
        }

        // Récupération, affectation et affichage de la position du joueur
        updatePlayer();

        // Vérification si la partie est terminée
        if (game.checkWinner() > -1) {
            // Partie terminée, il y a un gagnant
            int winner = game.checkWinner();

            textAlign(CENTER, CENTER);
            color(players[winner].color);
            textSize(50);
            text("Le joueur " +  (winner + 1) + " a gagné !", width/2, height/2 - 30);

            noFill();
            strokeWeight(2);
            stroke(players[winner].color);
            rectMode(CENTER);
            rect(width/2, height/2 + 30, 250, 50, 20);
            textSize(35);
            text("Recommencer", width/2, height/2 + 30);
            textAlign(LEFT);

            for (Player player : players) {
                if (player != null && player.center.x > width/2 - 150 && player.center.x < width/2 + 150 && player.center.y > height/2 - 75 && player.center.y < height/2 + 75) {
                    restartButtonPressed++;
                }
            }

            if (restartButtonPressed > 30) {
                // Si le bouton recommencer a été appuyé pendant 30 frames : nouvelle partie
                restartButtonPressed = 0;
                game = new Game(players, ball);
            }

        } else {
            // Appel de Game pour calculer les scores, s'il faut faire rebontir la balle etc...
            game.draw();
        }
    }

    void updatePlayer() {
        // Récupération et affectation de la position du joueur

        // Récupération des squelettes
        ArrayList<KSkeleton> skeletonArray = kinect.getSkeleton3d();
        for (int i = 0; i < skeletonArray.size(); i++) {
            KSkeleton skeleton = skeletonArray.get(i);
            if (skeleton.isTracked() && i < NOMBRE_JOUEURS) {
                KJoint[] joints = skeleton.getJoints(); // Contient tous les os du joueur

                // Contient la position de la main et du coude
                KJoint hand;
                KJoint elbow;

                // Choix de la main la plus en avant si on ne sait pas encore si le joueur est droitié ou gaucher
                if (players[i] == null) {
                    // Si le joueur n'est pas encore instancié, instanciation d'un nouveau joueur
                    // pour l'instant on utilise la position de la main la plus en avant
                    if (joints[KinectPV2.JointType_HandRight].getZ() < joints[KinectPV2.JointType_HandLeft].getZ()) {
                        hand = joints[KinectPV2.JointType_HandRight];
                        elbow = joints[KinectPV2.JointType_ElbowRight];
                    } else {
                        hand = joints[KinectPV2.JointType_HandLeft];
                        elbow = joints[KinectPV2.JointType_ElbowLeft];
                    }
                } else {
                    switch (players[i].rightHanded) {
                        case 1:
                            // Si le joueur est droitier, on lui donne la position de la main droite
                            hand = joints[KinectPV2.JointType_HandRight];
                            elbow = joints[KinectPV2.JointType_ElbowRight];
                            break;
                        case 0:
                            // S'il est gaucher, la gauche
                            hand = joints[KinectPV2.JointType_HandLeft];
                            elbow = joints[KinectPV2.JointType_ElbowLeft];
                            break;
                        default:
                            // Si pas encore déterminé, on lui donne la main la plus en avant
                            // Après quelques sec. la classe Player déterminera s'il est gaucher ou droitier
                            // et affectera la variable rightHeanded à 0 ou 1 selon la main la plus utilisée
                            if (joints[KinectPV2.JointType_HandRight].getZ() < joints[KinectPV2.JointType_HandLeft].getZ()) {
                                players[i].setHand(true);   // On dit à la classe Player que la main utilisée est la droite
                                hand = joints[KinectPV2.JointType_HandRight];
                                elbow = joints[KinectPV2.JointType_ElbowRight];
                            } else {
                                players[i].setHand(false);  // On dit à la classe Player que la main utilisée est la gauche
                                hand = joints[KinectPV2.JointType_HandLeft];
                                elbow = joints[KinectPV2.JointType_ElbowLeft];
                            }
                            break;
                    }
                }

                // Convertion des valeurs de la Kinect en pixels
                PVector elbowVector = new PVector(elbow.getX() * FACTEURHORIZONTAL * width / 2 + width/2, -elbow.getY() / FACTEURVERTICAL * height / 2 + height/2, elbow.getZ());
                PVector handVector = new PVector(hand.getX() * FACTEURHORIZONTAL * width / 2 + width / 2, -hand.getY() / FACTEURVERTICAL * height / 2 + height/2, hand.getZ());

                // Calcul de l'angle de la raquette (angle coude-main)
                float angleX;
                /* Si la main se trouve plus en avant et plus haut que le coude
                +-------------------------------------------------------+
                |                                                       |
                |             hand                                      |
                |               X                                       |
                |              /|                                       |
                |             / |                                       |
                |            /  |                                       |
                |           /   |                                       |
                |          /    |                         Kinect        |
                |         / \α  |                          +-+          |
                |        X------+                          +-+          |
                |      elbow                                            |
                |                                                       |
                | <------|------|---------------------------|---------- |
                | Z     1.2     1                           0           |
                |                                                       |
                +-------------------------------------------------------+
                */
                if (hand.getZ() < elbow.getZ() && hand.getY() < elbow.getY())
                    angleX = (float) Math.atan((hand.getY() - elbow.getY()) / (elbow.getZ() - hand.getZ()));

                /* Si la main se trouve plus en avant et plus bas que le coude
                +-------------------------------------------------------+
                |                                                       |
                |      elbow                                            |
                |        X------+                                       |
                |         \ /α  |                                       |
                |          \    |                                       |
                |           \   |                                       |
                |            \  |                                       |
                |             \ |                         Kinect        |
                |              \|                          +-+          |
                |               X                          +-+          |
                |             hand                                      |
                |                                                       |
                | <------|------|---------------------------|---------- |
                | Z     1.2     1                           0           |
                |                                                       |
                +-------------------------------------------------------+
                */
                else if (hand.getZ() < elbow.getZ() && hand.getY() > elbow.getY())
                    angleX = (float) -Math.atan((elbow.getY() - hand.getY()) / (elbow.getZ() - hand.getZ()));
                /* Si la main se trouve plus en arrière et plus haut que le coude
                +-------------------------------------------------------+
                |                                                       |
                |      hand                                             |
                |        X                                              |
                |        |\                                             |
                |        | \                                            |
                |        |  \                                           |
                |        |   \                                          |
                |        |    \                           Kinect        |
                |        |  B/ \ \α=180-B                  +-+          |
                |        +------X                          +-+          |
                |             elbow                                     |
                |                                                       |
                | <---------------------------------------------------+ |
                | Z     1.2     1                           0           |
                |                                                       |
                +-------------------------------------------------------+
                */
                else if(hand.getZ() > elbow.getZ() && hand.getY() > elbow.getY())
                    angleX = (float) (Math.PI - Math.atan((hand.getY() - elbow.getY()) / (hand.getZ() - elbow.getZ())));
                /* Si la main se trouve plus en arrière et plus bas que le coude
                +-------------------------------------------------------+
                |                                                       |
                |             elbow                                     |
                |        +------X                                       |
                |        |  B\ / /α=180-α                              |
                |        |    /                                         |
                |        |   /                                          |
                |        |  /                                           |
                |        | /                              Kinect        |
                |        |/                                +-+          |
                |        X                                 +-+          |
                |      hand                                             |
                |                                                       |
                | <---------------------------------------------------+ |
                | Z     1.2     1                           0           |
                |                                                       |
                +-------------------------------------------------------+
                */
                else if (hand.getZ() > elbow.getZ() && hand.getY() < elbow.getY())
                    angleX = (float)-(Math.PI - atan((elbow.getY() - hand.getY()) / (hand.getZ() - elbow.getZ())));
                else
                    angleX = 0;

                angleX -= Math.PI / 2;

                // Calcul de l'angle main-coude
                float angleZ;
                float deltaX, deltaY;
                deltaX = Math.abs(elbow.getX()-hand.getX());
                deltaY = Math.abs(elbow.getY()-hand.getY());
                if (hand.getX() > elbow.getX() && hand.getY() > elbow.getY())
                    angleZ = (float) (Math.toRadians(90) - Math.atan(deltaY/deltaX));
                else if (hand.getX() < elbow.getX() && hand.getY() > elbow.getY())
                    angleZ = (float) (Math.toRadians(270) + Math.atan(deltaY/deltaX));
                else if (hand.getX() < elbow.getX() && hand.getY() < elbow.getY())
                    angleZ = (float) (Math.toRadians(180) - Math.atan(deltaX/deltaY));
                else
                    angleZ = (float) (Math.toRadians(180) + Math.atan(deltaX/deltaY));

                int playerColor  = skeleton.getIndexColor(); // Récupération de la couleur du joueur

                // Création du nouveau joueur ou mis à jour de la position du joueur
                if(players[i] == null)
                    players[i] = new Player(this, elbowVector, handVector, angleX, angleZ, playerColor);
                else
                    players[i].updatePosition(elbowVector, handVector, angleX, angleZ, playerColor);
            }

        }


        for (Player player : players) {
            if (player != null) {
                // Affichage du joueur
                player.display();
            }
        }
    }

    public void keyPressed()
    //fonctions pour les options sur les touches
    //implemente juste un reset du programme sur le "enter" pour l'instant
    {
        switch(keyCode)
        {
            case 13://ENTER
                println("Reset...");
                players = new Player[NOMBRE_JOUEURS];
                game = new Game(players, ball);
                break;
        }
    }
}
