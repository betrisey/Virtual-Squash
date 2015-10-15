package ninja.sam.virtualsquash;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class Ball {
    private PApplet parent;
    public PVector position= new PVector(300,150,200);
    public PVector speed = new PVector(10,10,10);
    private static int  ballMin = 150;
    private static int  ballMaxi = 500;
    private static int  cW;
    private static int  cH;
    private static int  scW;
    private static int  scH;
    public int gameOver;
    public boolean sens = false;	// true = en direction du mur et false dans notre direction
    private PFont font;

    public Ball(PApplet parent,int cameW,int cameH,int screenW,int screenH)
    {
        //affectations de la fenêtre dans parent
        this.parent = parent;

        //affectation des constantes
        cW = cameW;
        cH = cameH;
        scW = screenW;
        scH = screenH;
        font = parent.loadFont("SegoePrint-Bold-75.vlw");

    }

    public void ballReset()
    //Reset de la balle et de sa vitesse
    {
        position.set(300,150,200);
        speed.set(10,10,10);
        sens = false;
        gameOver = 0;
    }
    public void move()
    {
        //REBONDS

        //Murs

        //fond
        if( position.z < ballMin)
        {
            speed.z = -speed.z;
            sens = false;

        }
        //Cotes gauche et droite
        //change la direction de la balle si elle doit quitter l'écran
        if(position.x > cH || position.x < 0)
        {
            speed.x = -speed.x;
        }
        //en haut et en bas
        if(position.y > cW || position.y < 0)
        {
            speed.y = -speed.y;
        }

        //En cas de sorti de l'écran
        //limitation de la taille de la balle
        if(position.z > ballMaxi)
        {
            position.z = ballMaxi;
            gameOver++;
            //PApplet.println(gameOver);
            //timeout si on la tape pas asser rapidement = game over
            if(gameOver >= 200 && !sens)
            {

                speed.z = -speed.z;

                speed.x = 0;
                speed.y = 0;
                speed.z = 0;
                int g;

                for(g=0;g <10000;g++)
                {
                    parent.textFont(font, 100);
                    parent.text("Perdu", 300, 200);
                    //PApplet.println(gameOver);
                }
            }
        }

        //déplace la balle en fonction du vector move (z pour la profondeur)
        position.add(speed);
    }

    public void bounce(PVector direction)
    //Algorithme de rebonds sur la raquette
    {
        PVector tmp = new PVector();
        direction.normalize();
        PVector mirror = new PVector();
        mirror.set(speed);
        tmp.set(direction);
        tmp.mult(speed.dot(direction));
        tmp.mult(-2);
        mirror.add(tmp);
        speed.set(mirror);

        //test sur le z de la balle pour que l'on reprenne de la bonne mainere la balle et que z finisse toujours en négatif
        if(speed.z > 0)
        {
            speed.z = -(PApplet.abs(speed.z)+1);
        }
		/*
		if(PApplet.abs(speed.z) > 2)
		{
			speed.z = -3;
		}*/



    }

    public void display()
    //fonctions d'affichage de la balle
    {
        //Décalage en x ou en y
        float shiftX  = 0;
        float shiftY  = 0;

        //Décalage en %
        float percentageScreenX = 0;
        float percentageScreenY = 0;

        //Le nombre de px de décalage
        float decalagePerspective = 160;

        // le décalage en regardant juste sa taille(z) peut aller de 0 a decalagePerspective
        shiftX = decalagePerspective-((position.z-ballMin)/(ballMaxi-ballMin))*decalagePerspective;
        shiftY = decalagePerspective-((position.z-ballMin)/(ballMaxi-ballMin))*decalagePerspective;

        //si c'est dans la moitié gauche de l'écran
        if(position.x <=cW/2)
        {
            //on récupère le pourcentage de ou on est par rapport au millieu
            percentageScreenX = 1-(position.x/(cW/2));

            //puis on multiplie le décalage absolue par le décalage en %
            shiftX = shiftX*percentageScreenX;

        }//dans la moitié droite
        else if(position.x > cW/2)
        {
            percentageScreenX = (position.x-(cW/2))/(cW/2);
            shiftX = -shiftX*percentageScreenX;
        }

        //si c'est dans la moitil du haut
        if(position.y <= cH/2)
        {
            percentageScreenY = 1-(position.y/(cH/2));
            shiftY = shiftY*percentageScreenY;
        }//moitié du bas
        else if(position.y > cH/2)
        {
            percentageScreenY = (position.y-(cH/2))/(cH/2);
            shiftY = -shiftY*percentageScreenY;
        }

        //afffichage
        parent.fill(0,0,0,255);
        parent.stroke(0,0,0,255);
        parent.ellipse((position.x+shiftX)/cH*scH,(position.y+shiftY)/cW*scW,position.z/20/cH*scH,position.z/20/cW*scW);
    }
}
