import KinectPV2.KinectPV2;
import processing.core.*;

import KinectPV2.*;

import java.util.Date;

public class Main extends PApplet {
    private static final long serialVersionUID = 1L;
    //private SimpleOpenNI  context;
    private int index, nbrPointsMain;
    private int[] valDepth;
    private PVector newDepth = new PVector (320,240,1000);
    private PVector lastDepth= new PVector(320,240,500);
    private int lastXmin,lastXmax,lastYmin,lastYmax;
    private float varLerp = 0.8f;
    private PFont font;
    private int rayon = 50;
    private int score = 0,multScore = 0,combo = 0,bestCombo = 0;
    private int timeout = 0;
    private boolean enableOnHit = false;
    Ball ball;
    Raquette raquette;
    PImage img = new PImage();

    private KinectPV2 kinect;

    private static int LARGEUR_CAMERA,LONGUEUR_CAMERA,LARGEUR_ECRAN,LONGUEUR_ECRAN;


    private long lastLogTime = System.currentTimeMillis();
    private void logFunction(String functionName) {
        long timeDiff = System.currentTimeMillis() - lastLogTime;
        lastLogTime = System.currentTimeMillis();

        System.out.println(functionName + ":\t" + timeDiff + "ms");
    }


    public void initConst()
    //Initalisation des constant plus construction de la balle déclarer dans main qui utilise ces constants
    {
        /*LARGEUR_CAMERA = 480;
        LONGUEUR_CAMERA = 640;
        LONGUEUR_ECRAN = 800;
        LARGEUR_ECRAN = 600;*/

        LARGEUR_CAMERA = 424;
        LONGUEUR_CAMERA = 512;
        LONGUEUR_ECRAN = 1680;
        LARGEUR_ECRAN = 1050;

        ball = new Ball(this, LARGEUR_CAMERA, LONGUEUR_CAMERA,LARGEUR_ECRAN,LONGUEUR_ECRAN);
        raquette = new Raquette(this, LARGEUR_CAMERA, LONGUEUR_CAMERA,LARGEUR_ECRAN,LONGUEUR_ECRAN);
    }

    public void settings() {
        size(1680, 1050);
    }

    public void setup()
    //Setup
    {
        //initialise les constants
        initConst();

        //création de la fenetre
        frameRate(60);


        // Initialise la Kinect
        kinect = new KinectPV2(this);
        kinect.enableDepthImg(true);
        kinect.init();

        /*Charge la camera
        context = new SimpleOpenNI(this,SimpleOpenNI.RUN_MODE_MULTI_THREADED);
//		context = new SimpleOpenNI(this,SimpleOpenNI.RUN_MODE_SINGLE_THREADED);
        context.setMirror(true);
        context.enableDepth(LONGUEUR_CAMERA,LARGEUR_CAMERA,100);*/

        //améliore la fluidité
        smooth();

        //charge la police d'écriture
        font = loadFont("SegoePrint-Bold-75.vlw");
        textFont(font, 20);

        //charge le fond
        //Attention : est obliger d'avoir la même taille que la fenetre ou ca plante ou alors il faut juste charger une image
        img = loadImage("SalleSquash.jpg");
        img.resize(LONGUEUR_ECRAN, LARGEUR_ECRAN);
        logFunction("Setup");
    }




    public void draw()
    //draw
    {
        imageMode(PConstants.CORNER);
        //met a jour l'image de profondeur, format 8bit gris
        image(kinect.getDepthImage(), 0, 0);

        //stock la profondeur de chaque point dans un tableau
        //valDepth=context.depthMap();
        valDepth = kinect.getRawDepthData();
        logFunction("getRawDepthData");
        //valDepth = new int[217088];

        //le tour des objet en blanc avec de la transparence et l'interieur en blanc aussi
        stroke(255, 255, 255, 200);
        fill(255, 255, 255, 0);
        background(img);
        logFunction("background");

        //déplace la ball, cherche la main , affiche la ball
        ball.move();
        logFunction("ball.move");
        ball.display();
        logFunction("ball.display");
        HandGenerator();
        logFunction("handgenerator");

        fill(0, 0, 0, 255);
        stroke(0, 0, 0, 255);

        //affiche le score + best combo
        textFont(font, 20);
        text("Score : " + score, 20, 20);

        textFont(font, 20);
        text("Meilleur combo : " + bestCombo, 420, 20);

        textFont(font, 20);
        text("Multiplicateur : " + multScore, 180, 20);

        if(enableOnHit && timeout < 300)
        {
            timeout++;
            textFont(font, 40);
            text("+" + combo,300,70);
        }
        else
        {
            timeout = 0;
            combo = 0;
            enableOnHit = false;
            multScore = 0;
        }
        logFunction("affiche score");
    }

    public void HandGenerator()
    //fonction qui va chercher la main puis récuperer divers point :
    //lastDepth : le point central de la main
    //newDepth : récupere le nouveau point central a chaque update et le met dans lastDepth
    //Pa,Pb,Pc : les 3 points sur la main pour trouver le vecteur sur le centre de gravité et où la main regarde ils sont situé sur l'extrémité de la main sur l'axe x et y du centre
    {
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

        logFunction("handgenerator recuperation angles");


        //Décommenter pour afficher le rectangle ou l'ont fait la recherche de la main
		/*rectMode(CORNERS);
		fill(255, 255, 255, 150);
		rect(lastXmin, lastYmin, lastXmax, lastYmax);
		rectMode(CORNER);*/



        //Double boucle qui prend chaque point de la main avec x et y
        for(y = lastYmin; y < lastYmax; y++)
        {
            System.out.println(lastYmax);
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

        logFunction("handgenerator recherche main");

        //Trouve le point central du bras qui nous fournit le manche
        handle.set((handle1.x+handle2.x)/2,(handle1.y+handle2.y)/2,getDepth((handle1.x+handle2.x)/2,(handle1.y+handle2.y)/2));


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
		/*ellipse(Pa.x, Pa.y, 7, 7);
		ellipse(Pb.x, Pb.y, 7, 7);
		ellipse(Pc.x, Pc.y, 7, 7);*/

        //affiche le point central sur le bras
        //ellipse(handle.x, handle.y, 7, 7);

        //affiche la raquette
        raquette.display(lastDepth, handle);
    }


    public void keyPressed()
//fonctions pour les options sur les touches
//implemente juste un reset du programme sur le "enter" pour l'instant
    {
        switch(keyCode)
        {
            case LEFT:
                println("left");
                break;
            case RIGHT:
                println("right");
                break;
            case UP:
                println("up");
                break;
            case DOWN:
                println("down");
                break;
            case ENTER:
                println("Reset...This operation may take a long time please be patient ... ... ... ... Reset completed ! ");
                newDepth.set(320,240,1000);
                lastDepth.set(320, 240,500);
                raquette.raquetteReset();
                score = 0;
                bestCombo = 0;
                combo = 0;
                multScore = 0;
                ball.ballReset();
                break;
        }
    }

    private int getDepth(double x, double y)
    //fonctions qui renvoie la valeur z d'un point en x et y
    {
        int i;
        //i = (int)x + (int)y * context.depthWidth();
        i = (int)x + (int)y * kinect.getDepthImage().width;
        return valDepth[i];
    }


    // -----------------------------------------------------------------
    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "Main" });
    }
}
