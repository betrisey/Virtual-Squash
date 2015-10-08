package ninja.sam.virtualsquash;

import KinectPV2.KinectPV2;
import processing.core.*;
import KinectPV2.*;

import java.util.ArrayList;

public class Main extends PApplet {

    private PFont font;
    private int timeout;
    private int nbreJoueursDetectes;
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


        public void HandGenerator()
        //fonction qui va chercher la main puis récuperer divers point :
        //lastDepth : le point central de la main
        //newDepth : récupere le nouveau point central a chaque update et le met dans lastDepth
        //Pa,Pb,Pc : les 3 points sur la main pour trouver le vecteur sur le centre de gravité et où la main regarde ils sont situé sur l'extrémité de la main sur l'axe x et y du centre
        {
            /*
            //sert pour le lerp a la fin
            nbrPointsMain = 1;

            //Jvais pas faire un dessin
            int x,y;

            //décrit dans l'en-tête
            PVector Pa = new PVector();
            PVector Pb = new PVector();
            PVector Pc = new PVector();
            //3 variable qui difinisse si les 3 point sur la main ont été défini ou pas
            boolean ok1 = false;
            boolean ok2 = false;
            boolean ok3 = false;

            //pour le manche de la raquette : handle1 et handle2 sont les 2 extremités du bras sur le bord du rectangle et handle est le centre du bras a la limite du rectangle
            PVector handle1 = new PVector();
            PVector handle2 = new PVector();
            PVector handle = new PVector();
            //2 variable pour savoir si on as les 2 extrémité du bras
            boolean handleFirst = false;
            boolean handleLast = false;

            //Récupére les 2 angle oposer du rectangle
            lastXmin = Math.max((int) (lastDepth.x - (rayon * 1.5)), 0);
            //lastXmax = Math.min((int) (lastDepth.x + (rayon * 1.5)), context.depthWidth());
            lastXmax = Math.min((int) (lastDepth.x + (rayon * 1.5)), kinect.getDepthImage().width);
            lastYmin = Math.max((int) (lastDepth.y - (rayon * 1.5)), 0);
            //lastYmax = Math.min((int)(lastDepth.y + (rayon * 1.5)), context.depthHeight());
            lastYmax = Math.min((int)(lastDepth.y + (rayon * 1.5)), kinect.getDepthImage().height);

            //Décommenter pour afficher le rectangle ou l'ont fait la recherche de la main
            rectMode(CORNERS);
            fill(255, 255, 255, 150);
            rect(lastXmin, lastYmin, lastXmax, lastYmax);
            rectMode(CORNER);


            //Double boucle qui prend chaque point de la main avec x et y
            for(y = lastYmin; y < lastYmax; y++)
            {
                for(x = lastXmin; x < lastXmax; x++)
                {
                    //récupère la profondeur(z) de chaque point
                    index = getDepth(x,y);

                    //Controle pour chaque fois si il faut partie de la main
                    //pour ca l'on compare ca pronfondeur avec cell qu'il avait avant ne doit pas dépasser 50
                    if(index > (lastDepth.z - 50) && index < (lastDepth.z+50))
                    {

                        //test + génére Pb : le point gauche sur l'axe du x du point central
                        if(x-1 <= lastDepth.x && x+1 >= lastDepth.x && !ok1)
                        {
                            Pb.set(x,y,index);
                            ok1 = true;

                        }//test + génére Pa : le point central sur l'axe du y du point central
                        else if(y-1 <= lastDepth.y && y+1 >= lastDepth.y && !ok2)
                        {
                            Pa.set(x,y,index);
                            ok2 = true;
                        }//test + génére Pc : le point droite sur l'axe du x du point central
                        else if(y-1 <= lastDepth.y && y+1 >= lastDepth.y && Pc.x < x)
                        {
                            Pc.set(x,y,index);
                            ok3 = true;

                        }

                        //si l'on as les 3 point on génére la raquette
                        if(ok1 || ok2 || ok3)
                        {
                            raquette.setRaquette(lastDepth, Pa, Pb, Pc);
                        }


                        //suite de test pour trouver ou est le manche de la raquette
                        //control pour les points de la main si il sont sur la moitié basse du bord gauche du rectangle
                        //(a partir de la moitié on considére que le bras ne sortiras par le haut a cause de risque de confilt sinon)
                        if(x-1 <= lastXmin && x+1 >= lastXmin && y > lastYmin+(lastYmax-lastYmin)/2)
                        {
                            //si le point est le premier que l'on trouve sur le bord c'est forcement la première extremité du bras
                            if(!handleFirst)
                            {
                                handleFirst = true;
                                handle1.set(x,y,index);
                            }//sinon si on as la premiere extemité on le met dans l'autre extremité si le point est plus loint sur le bord
                            else if(handle2.y < y && !handleLast)
                            {
                                handle2.set(x,y,index);
                            }

                        }//control pour les points de la main si il sont sur la moitié basse du bord droit du rectangle
                        else if(x-1 <= lastXmax && x+1 >= lastXmax && y > lastYmin+(lastYmax-lastYmin)/2)
                        {
                            if(!handleLast)
                            {
                                handleLast = true;
                                handle2.set(x,y,index);
                            }
                            else if(handle1.y < y && !handleFirst)
                            {
                                handle1.set(x,y,index);
                            }
                        }//control pour les points de la main si il sont sur le bord du bas du rectangle
                        else if(y-1 <= lastYmax && y+1 >= lastYmax)
                        {
                            if(!handleFirst)
                            {
                                handleFirst = true;
                                handle1.set(x,y,index);
                            }
                            else if(handle2.x < x && !handleLast)
                            {
                                handle2.set(x,y,index);
                            }
                        }


                        //incrémente le nombre de point de la main et set newdepth sur le point actuelle(le dernier de la main)
                        nbrPointsMain++;
                        newDepth.set(newDepth.x + x, newDepth.y + y, newDepth.z + index);

                        //Test si la balle touche la raquette
                        if(ball.position.x > lastXmin && ball.position.x < lastXmax && ball.position.y >lastYmin && ball.position.y < lastYmax && ball.position.z > 400 && ball.sens == false)
                        {
                            //Gestion scort,combo,...
                            enableOnHit = true;
                            combo++;
                            if(combo > bestCombo)
                            {
                                bestCombo = combo;
                            }
                            timeout = 0;

                            multScore = combo/2 + 1;
                            score = score + multScore;


                            //change le sens de la balle
                            ball.sens = true;
                            //change la couleur
                            fill(255,0,0);
                            //reset le timer de gameover
                            ball.gameOver = 0;
                            //fait rebondir
                            ball.bounce(raquette.getDirection());

                            textFont(font, 50);
                            text("+ 1",300,2550);


                        }
                    }
                }
            }

            //Trouve le point central du bras qui nous fournit le manche
            //handle.set((handle1.x+handle2.x)/2,(handle1.y+handle2.y)/2,getDepth((handle1.x+handle2.x)/2,(handle1.y+handle2.y)/2));


            //trouve le point central
            newDepth.set(newDepth.x / nbrPointsMain,
                    newDepth.y / nbrPointsMain,
                    newDepth.z / nbrPointsMain);

            //lerp du point central
            newDepth.set(
                    lerp(lastDepth.x, newDepth.x, varLerp),
                    lerp(lastDepth.y, newDepth.y, varLerp),
                    lerp(lastDepth.z, newDepth.z, varLerp));

            fill(255,0,0,150);
            noStroke();
            ellipseMode(CENTER);
            //affiche le point central
            //ellipse(newDepth.x, newDepth.y, 10, 10);
            lastDepth.set(newDepth);

            //affiche pa,pb,pc
            ellipse(Pa.x, Pa.y, 7, 7);
            ellipse(Pb.x, Pb.y, 7, 7);
            ellipse(Pc.x, Pc.y, 7, 7);

            //affiche le point central sur le bras
            //ellipse(handle.x, handle.y, 7, 7);

            //affiche la raquette
            //raquette.display(lastDepth, handle);
            raquette.display(lastDepth, handle);
            */
        }


    void drawPlayer() {
        // Récupération des squelettes
        ArrayList<KSkeleton> skeletonArray = kinect.getSkeleton3d();
        nbreJoueursDetectes = 0;
        for (int i = 0; i < skeletonArray.size(); i++) {
            KSkeleton skeleton = skeletonArray.get(i);
            if (skeleton.isTracked() && i < NOMBRE_JOUEURS) {
                KJoint[] joints = skeleton.getJoints();

                KJoint hand = joints[KinectPV2.JointType_HandRight];
                KJoint elbow = joints[KinectPV2.JointType_ElbowRight];

                // Ajustement des distances
                float facteurHorizontal = 1.2f;
                float facteurVertical = 0.5f;

                PVector elbowVector = new PVector(elbow.getX() * facteurHorizontal * width / 2 + width/2, -elbow.getY() / facteurVertical * height / 2 + height/2, elbow.getZ());
                PVector handVector = new PVector(hand.getX() * facteurHorizontal * width / 2 + width / 2, -hand.getY() / facteurVertical * height / 2 + height/2, hand.getZ());

                float angle = (float) (PI/2-(Math.atan2(-hand.getY(), hand.getX()) - Math.atan2(-elbow.getY(), elbow.getX())));


                int playerColor  = skeleton.getIndexColor();
                if(players[i] == null)
                    players[i] = new Player(this, elbowVector, handVector, angle, playerColor, i==0);
                else
                    players[i].updatePosition(elbowVector, handVector, angle, playerColor);

                if (i == 0) {
                    if (players[i+1] == null)
                        players[i+1] = new Player(this, new PVector(elbowVector.x + 100, elbowVector.y, elbowVector.z), new PVector(handVector.x + 100, handVector.y, handVector.z), angle, playerColor, false);
                    else
                        players[i+i].updatePosition(new PVector(elbowVector.x + 100, elbowVector.y, elbowVector.z), new PVector(handVector.x + 100, handVector.y, handVector.z), angle, playerColor);
                }


                //stroke(playerColor);
                //strokeWeight(10);
                //line(elbowVector.x, elbowVector.y, handVector.x, handVector.y);




                nbreJoueursDetectes++;
            }


        }

        //Test si la balle touche la raquette
        for (int i=0; i< players.length; i++) {
            Player player = players[i];

            if (player != null)
                player.display();

            if(player != null && Math.sqrt(Math.pow(player.center.x - ball.position.x, 2) + Math.pow(player.center.y - ball.position.y, 2)) <= player.size && ball.position.z > 400 && ball.sens == false)
            {
                // La balle touche la raquette

                //Gestion score
                enableOnHit = true;
                timeout = 0;

                player.score++;


                //change le sens de la balle
                ball.sens = true;
                //change la couleur
                fill(255,0,0);
                //reset le timer de gameover
                ball.gameOver = 0;
                //fait rebondir
                ball.bounce(player.getDirection());

                textFont(font, 50);
                text("+ 1",300,2550);
            }
        }
    }



    public void initConst()
    //Initalisation des constant plus construction de la balle déclarer dans main qui utilise ces constants
    {
        ball = new Ball(this, LARGEUR_CAMERA, LONGUEUR_CAMERA,LARGEUR_ECRAN,LONGUEUR_ECRAN);
        players = new Player[NOMBRE_JOUEURS];
    }

    public void settings() {
        size(1680, 945, P3D);
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

        //améliore la fluidité
        smooth();

        //charge la police d'écriture
        font = loadFont("SegoePrint-Bold-75.vlw");
        textFont(font, 20);

        //charge le fond
        //Attention : est obliger d'avoir la même taille que la fenetre ou ca plante ou alors il faut juste charger une image
        img = loadImage("SalleSquash.jpg");
        img.resize(1680, 945);
    }

    public void draw()
    {
        //le tour des objet en blanc avec de la transparence et l'interieur en blanc aussi
        stroke(255, 255, 255, 200);
        fill(255, 255, 255, 0);
        background(img);

        // Affiche l'image de la kinect en transparence
        //tint(255, 60);
        image(kinect.getColorImage(), 0, 0, 1680, 945);

        //déplace la ball, cherche la main , affiche la ball
        ball.move();
        ball.display();


        fill(0, 0, 0, 255);
        stroke(0, 0, 0, 255);

        text("FPS : " + frameRate, 330, 20);
        text("Joueurs :" + nbreJoueursDetectes, 210, 20);

        //affiche le score
        textFont(font, 20);
        for(int i=0; i<NOMBRE_JOUEURS; i++) {
            if (players[i] != null)
                text("Score joueur " + (i+1) + " : " + players[i].score, 20, i*30 + 20);
        }

        // Déplacement de la main du joueur
        drawPlayer();

        if(enableOnHit && timeout < 300)
        {
            timeout++;
        }
        else
        {
            timeout = 0;
            enableOnHit = false;
        }
    }


    public void keyPressed()
//fonctions pour les options sur les touches
//implemente juste un reset du programme sur le "enter" pour l'instant
    {
        switch(keyCode)
        {
            case ENTER:
                println("Reset...");
                players = new Player[NOMBRE_JOUEURS];
                ball.ballReset();
                break;
        }
    }

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "ninja.sam.virtualsquash.Main" });
    }
}
