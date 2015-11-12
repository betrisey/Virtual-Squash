/*
Virtual Squash 2 - 2015 - Samuel Bétrisey
Classe Balle
Permet d'afficher une balle qui rebondit contre les murs
on peut la faire rebondir dans une direction avec la méthode bounce
et la faire accélérer avec la méthode accelerate
 */

package ninja.sam.virtualsquash;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Ball {
    private PApplet parent;
    public PVector position= new PVector(300,150,200);
    public PVector speed = new PVector(10,10,10);
    private static int  ballMin = 150;
    private static int  ballMaxi = 500;
    private int screenWidth;
    private int screenHeight;
    public int color;   // -1 = default color
    public boolean sens = false;	// true = en direction du mur et false dans notre direction

    public Ball(PApplet parent, int screenWidth, int screenHeight)
    {
        //affectations de la fenêtre dans parent
        this.parent = parent;

        //affectation des constantes
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void ballReset()
    //Reset de la balle et de sa vitesse
    {
        position.set(300,150,200);
        speed.set(10,10,10);
        sens = false;
    }
    public void move()
    {
        // Déplacement en prenant en compte les murs et le fond

        //fond
        if( position.z < ballMin)
        {
            speed.z = -speed.z;
            sens = false;
        }
        //Cotes gauche et droite
        //change la direction de la balle si elle doit quitter l'écran
        if(position.x > screenHeight || position.x < 0)
        {
            speed.x = -speed.x;
        }
        //en haut et en bas
        if(position.y > screenWidth || position.y < 0)
        {
            speed.y = -speed.y;
        }

        // La balle ne peut pas passer de dehors de l'écran vers l'arrière
        // par contre le joueur sera pénalisé s'il ne la fait pas rebondir assez vite
        if(position.z > ballMaxi)
        {
            position.z = ballMaxi;
        }

        // déplace la balle en fonction du vecteur speed (z pour la profondeur)
        position.add(speed);
    }

    public void bounce(PVector direction)
    // Algorithme de rebonds sur la raquette
    {
        // Norme du vecteur speed
        float norm = PApplet.sqrt(speed.x*speed.x + speed.y*speed.y + speed.z*speed.z);
        // On garde l'ancienne norme et on utilise le vecteur direction
        direction.normalize();
        speed.set(direction).mult(norm);

        //test sur le z de la balle pour que l'on reprenne de la bonne mainere la balle et que z finisse toujours en négatif
        if(speed.z > 0)
        {
            speed.z = -(PApplet.abs(speed.z)+1);
        }
    }

    public void display()
    //fonctions d'affichage de la balle
    {
        //Décalage en x ou en y
        float shiftX;
        float shiftY;

        //Décalage en %
        float percentageScreenX;
        float percentageScreenY;

        //Le nombre de px de décalage
        float decalagePerspective = 160;

        // le décalage en regardant juste sa taille(z) peut aller de 0 a decalagePerspective
        shiftX = decalagePerspective-((position.z-ballMin)/(ballMaxi-ballMin))*decalagePerspective;
        shiftY = decalagePerspective-((position.z-ballMin)/(ballMaxi-ballMin))*decalagePerspective;

        //si c'est dans la moitié gauche de l'écran
        if(position.x <= screenWidth /2)
        {
            //on récupére le pourcentage de ou on est par rapport au millieu
            percentageScreenX = 1-(position.x/(screenWidth /2));

            //puis on multiplie le décalage absolue par le décalage en %
            shiftX = shiftX*percentageScreenX;

        }//dans la moitié droite
        else if(position.x > screenWidth /2)
        {
            percentageScreenX = (position.x-(screenWidth /2))/(screenWidth /2);
            shiftX = -shiftX*percentageScreenX;
        }

        //si c'est dans la moitil du haut
        if(position.y <= screenHeight /2)
        {
            percentageScreenY = 1-(position.y/(screenHeight /2));
            shiftY = shiftY*percentageScreenY;
        }//moitié du bas
        else if(position.y > screenHeight /2)
        {
            percentageScreenY = (position.y-(screenHeight /2))/(screenHeight /2);
            shiftY = -shiftY*percentageScreenY;
        }

        //afffichage de la balle avec une image
        PShape ballImage = parent.loadShape("assets/ball.svg");

        // Si une couleur est définie, on rempli la balle de cette couleur
        if (color != 0) {
            ballImage.disableStyle();
            parent.fill(color);
        }

        parent.shape(ballImage, position.x+shiftX,position.y+shiftY,position.z/10+10,position.z/10+10);
    }

    public void accelerate(float factor) {
        speed.mult(factor);
    }
}
