package ninja.sam.virtualsquash;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Player {
    private PApplet parent;

    public PVector elbow, center;
    public float angle, distance, width, height;
    public int score, color;

    private static int WIDTH = 100;
    private static int HEIGHT = 200;

    public Player(PApplet parent, PVector elbow, PVector center, float angle, int color) {
        this.parent = parent;
        this.elbow = elbow;
        this.center = center;
        this.angle = angle;
        this.color = color;
        this.width = WIDTH/center.z;
        this.height = HEIGHT/center.z;

        this.score = 0;
    }

    public void updatePosition(PVector elbow, PVector center, float angle, int color) {
        float lerp = 0.5f;
        this.elbow = new PVector(parent.lerp(this.elbow.x, elbow.x, lerp), parent.lerp(this.elbow.y, elbow.y, lerp), parent.lerp(this.elbow.z, elbow.z, lerp));
        this.center = new PVector(parent.lerp(this.center.x, center.x, lerp), parent.lerp(this.center.y, center.y, lerp), parent.lerp(this.center.z, center.z, lerp));
        this.angle = parent.lerp(this.angle, angle, lerp);
        this.distance = this.center.z;
        this.color = color;
    }

    void display() {
        /* Affchage avec image
        PImage raquetteImage = parent.loadImage("raquette.png");
        // Taille de la raquette selon la distance main-coude
        //raquetteImage.resize(0, (int)Math.sqrt(Math.pow(center.x-elbow.x, 2) + Math.pow(center.y-elbow.y, 2)));
        raquetteImage.resize(0, 500);

        parent.pushMatrix();
        parent.translate(center.x, center.y);
        parent.translate(-raquetteImage.width / 2, -raquetteImage.height / 4);

        float angleRaquette2D = (float) (Math.PI/2 - Math.atan((Math.abs(elbow.y - center.y)) / (Math.abs(elbow.x - center.x))));
        if(center.x < elbow.x)
            angleRaquette2D = (float)Math.PI*2 - angleRaquette2D;

        //if(center.y > elbow.y)
        //    angleRaquette2D = (float)Math.PI - angleRaquette2D;

        parent.rotate(angleRaquette2D);

        parent.rotateX((float)Math.atan((Math.abs(elbow.z-center.z))/(elbow.y-center.y)));

        parent.image(raquetteImage, 0, 0);
        parent.popMatrix();
        parent.text("Angle img raquette : " + Math.toDegrees(angleRaquette2D), 200,200);
        */


        // Affichage du manche de la raquette
        parent.stroke(color);
        parent.strokeWeight(10);
        parent.line(elbow.x, elbow.y, center.x, center.y);

        // Affichage de la raquette
        parent.pushMatrix();
        parent.translate(center.x, center.y);
        parent.rotateX(angle);
        //parent.rotateZ(center.y < elbow.y ? parent.atan((elbow.x-center.x)/(center.y-elbow.y)) : parent.atan((center.x-elbow.x)/(center.y-elbow.y)));
        parent.fill(color);
        parent.noStroke();
        parent.box(width, height, 15);
        parent.popMatrix();

        parent.text("x:"+Math.round(center.x)+" y:"+Math.round(center.y)+" z:"+Math.round(center.z*100)/100, 500,700);
    }

    public PVector getDirection() {
        PVector direction = new PVector((float)Math.cos(angle), (float)Math.sin(angle));

        return direction;
    }
}
