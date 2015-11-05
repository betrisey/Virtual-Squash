package ninja.sam.virtualsquash;

import KinectPV2.KinectPV2;
import processing.core.*;
import KinectPV2.*;

import java.util.ArrayList;

public class Main extends PApplet {

    private PFont font;
    private int timeout;
    private boolean enableOnHit = false;
    Ball ball;

    PImage img = new PImage();

    private KinectPV2 kinect;
    private static int LARGEUR_CAMERA = 424;
    private static int LONGUEUR_CAMERA = 512;
    private static int LONGUEUR_ECRAN = 1680;
    private static int LARGEUR_ECRAN = 945;

    private static int NOMBRE_JOUEURS = 2;

    private Player[] players;

    private Game game;

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "ninja.sam.virtualsquash.Main" });
    }

    public void settings() {
        size(LONGUEUR_ECRAN, LARGEUR_ECRAN, P3D);
    }

    public void setup()
    //Setup
    {
        //initialise les constants
        initConst();

        // Initialise la Kinect
        kinect = new KinectPV2(this);
        kinect.enableColorImg(true);
        kinect.enableSkeleton3DMap(true);
        kinect.init();

        //am�liore la fluidit�
        smooth();

        //charge la police d'�criture
        font = loadFont("SegoePrint-Bold-75.vlw");
        textFont(font, 20);

        //charge le fond
        //Attention : est obliger d'avoir la m�me taille que la fenetre ou ca plante ou alors il faut juste charger une image
        img = loadImage("SalleSquash.jpg");
        img.resize(LONGUEUR_ECRAN, LARGEUR_ECRAN);
    }

    public void initConst()
    //Initalisation des constant plus construction de la balle d�clarer dans main qui utilise ces constants
    {
        //ball = new Ball(this, LARGEUR_CAMERA, LONGUEUR_CAMERA,LARGEUR_ECRAN,LONGUEUR_ECRAN);
        ball = new Ball(this, LARGEUR_ECRAN, LONGUEUR_ECRAN,LARGEUR_ECRAN,LONGUEUR_ECRAN);

        players = new Player[NOMBRE_JOUEURS];

        game = new Game(players, ball);
    }

    public void draw()
    {
        //le tour des objet en blanc avec de la transparence et l'interieur en blanc aussi
        stroke(255, 255, 255, 200);
        fill(255, 255, 255, 0);
        background(img);

        // Affiche l'image de la kinect en transparence
        tint(255, 75);
        image(kinect.getColorImage(), 0, 0, LONGUEUR_ECRAN, LARGEUR_ECRAN);
        //tint(255, 255);

        fill(0, 0, 0, 255);
        stroke(0, 0, 0, 255);
        text("FPS : " + Math.round(frameRate), 250, 20);
        text("Joueurs detectes :" + kinect.getSkeleton3d().size(), 350, 20);

        //affiche le score
        textFont(font, 20);
        for(int i=0; i<NOMBRE_JOUEURS; i++) {
            if (players[i] != null) {
                text("Score joueur " + (i + 1) + " : " + players[i].score, 20, i * 30 + 20);
            }
        }

        if (game.maxScore > 0)
            text("Score max : " + game.maxScore, 20, 50);

        // D�placement de la main du joueur
        updatePlayer();

        game.draw();
    }

    void updatePlayer() {
        // R�cup�ration des squelettes
        ArrayList<KSkeleton> skeletonArray = kinect.getSkeleton3d();
        for (int i = 0; i < skeletonArray.size(); i++) {
            KSkeleton skeleton = skeletonArray.get(i);

            if (skeleton.isTracked() && i < NOMBRE_JOUEURS) {
                KJoint[] joints = skeleton.getJoints();

                KJoint hand;
                KJoint elbow;

                // Choix de la main la plus en avant si on ne sait pas encore si le joueur est droitié ou gaucher
                if (players[i] == null) {
                    if (joints[KinectPV2.JointType_HandRight].getZ() < joints[KinectPV2.JointType_HandLeft].getZ()) {
                        hand = joints[KinectPV2.JointType_HandRight];
                        elbow = joints[KinectPV2.JointType_ElbowRight];
                    } else {
                        hand = joints[KinectPV2.JointType_HandLeft];
                        elbow = joints[KinectPV2.JointType_ElbowLeft];
                    }
                } else {
                    System.out.println(players[i].rightHanded);
                    switch (players[i].rightHanded) {
                        case 1:
                            hand = joints[KinectPV2.JointType_HandRight];
                            elbow = joints[KinectPV2.JointType_ElbowRight];
                            break;
                        case 0:
                            hand = joints[KinectPV2.JointType_HandLeft];
                            elbow = joints[KinectPV2.JointType_ElbowLeft];
                            break;
                        default:
                            if (joints[KinectPV2.JointType_HandRight].getZ() < joints[KinectPV2.JointType_HandLeft].getZ()) {
                                players[i].setHand(true);
                                hand = joints[KinectPV2.JointType_HandRight];
                                elbow = joints[KinectPV2.JointType_ElbowRight];
                            } else {
                                players[i].setHand(false);
                                hand = joints[KinectPV2.JointType_HandLeft];
                                elbow = joints[KinectPV2.JointType_ElbowLeft];
                            }
                            break;
                    }
                }

                // Convertion des valeurs de la Kinect en pixels
                float facteurHorizontal = 1.2f;
                float facteurVertical = 0.5f;
                PVector elbowVector = new PVector(elbow.getX() * facteurHorizontal * width / 2 + width/2, -elbow.getY() / facteurVertical * height / 2 + height/2, elbow.getZ());
                PVector handVector = new PVector(hand.getX() * facteurHorizontal * width / 2 + width / 2, -hand.getY() / facteurVertical * height / 2 + height/2, hand.getZ());

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
                |         / \?  |                          +-+          |
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
                |         \ /?  |                                       |
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
                /* Si la main se trouve plus en arri�re et plus haut que le coude
                +-------------------------------------------------------+
                |                                                       |
                |      hand                                             |
                |        X                                              |
                |        |\                                             |
                |        | \                                            |
                |        |  \                                           |
                |        |   \                                          |
                |        |    \                           Kinect        |
                |        |  ?/ \ \?=180�-?                 +-+          |
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
                /* Si la main se trouve plus en arri�re et plus bas que le coude
                +-------------------------------------------------------+
                |                                                       |
                |             elbow                                     |
                |        +------X                                       |
                |        |  ?\ / /?=180�-?                              |
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

                int playerColor  = skeleton.getIndexColor(); // R�cup�ration de la couleur du joueur

                // Cr�ation du nouveau joueur ou mis � jour de la position du joueur
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
                ball.ballReset();
                game = new Game(players, ball);
                break;
        }
    }
}
