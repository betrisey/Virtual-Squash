import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;


public class Raquette {
    private PApplet parent;
    private PVector position = new PVector();
    private PVector direction = new PVector();
    private PVector oldHandle = new PVector(320,315,500);

    private static int  cW;
    private static int  cH;
    private static int  scW;
    private static int  scH;


    public Raquette(PApplet parent,int cameW,int cameH,int screenW,int screenH)
    {
        //affectation du parent
        this.parent = parent;
        cW = cameW;
        cH = cameH;
        scW = screenW;
        scH = screenH;
    }

    public void raquetteReset()
    {
        oldHandle = new PVector(320,315,500);
    }
    public void setRaquette(PVector center,PVector P1, PVector P2 ,PVector P3)
    //fonction de set de la raquette
    //récupère le centre de la main et les 3 points au extrémiter sur l'axe x et y du centre
    //puis calcule la direction du vecteur sur le centre de gravité
    {

        PVector t1 = new PVector();
        PVector t2 = new PVector();

        position.set(center);

        t1.set(P2);
        t1.sub(P1);
        t2.set(P3);
        t2.sub(P1);
        direction = t1.cross(t2);

        direction.normalize();
        direction.mult(100);
    }

    public PVector getDirection()
    //renvoie la direction
    {
        return direction;
    }

    public void display(PVector center, PVector handle)
    //affiche la raquette
    {
        parent.strokeWeight(5);
        parent.stroke(0, 0, 0, 255);
        parent.fill(255, 255, 255, 100);
        int width = 150;
        int height = 150;
        float a = 90;

        //si le manche na pas put etre calculer on récupère l'ancien
        if(handle.x == 0 && handle.y == 0)
        {
            handle.set(oldHandle);
        }

        //affiche le manche de la raquette
        parent.line(center.x/cH*scH, center.y/cW*scW, handle.x/cH*scH, handle.y/cW*scW);

        //calcul l'angle de la raquette sur le plan horizontal (sans aucun z pris en compte)
        a = PApplet.atan2(handle.y-center.y, center.x-handle.x);

        //il faut transformer l'angle du radian en degré
        a = (float)Math.toDegrees(a);

        //Crée le graphic qui servira d'image a afficher pour la raquette
        PGraphics g = parent.createGraphics(height, 150, PConstants.JAVA2D);

        //on comence le dessin puis décale le au centre ou l'on dessine
        g.beginDraw();

        //?
        //g.line(center.x, center.y, handle.x, handle.y);

        g.translate(width/2, height/2);

        g.strokeWeight(5);
        g.stroke(0, 0, 0, 255);
        g.fill(255, 255, 255, 100);

        //incline la raquette de -a puis créer l'ellipse
        g.rotate(PApplet.radians(-a));
        g.ellipse(0, 0,50*100/cH*scH/100,40*100/cW*scW/100);



        //fin du dessin
        g.endDraw();
        //mode de placement en center puis affichage

        parent.imageMode(PConstants.CENTER);
        parent.image(g, center.x/cH*scH, center.y/cW*scW);


        //set le manche dans l'ancien manche au cas ou il n'y en aurait pas pour la suite
        oldHandle.set(handle);
    }
}
