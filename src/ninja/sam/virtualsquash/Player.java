package ninja.sam.virtualsquash;

import processing.core.PApplet;
import processing.core.PVector;

public class Player {
    private PApplet parent;

    public PVector elbow, center;
    public float angle, size;
    public int score, color;
    boolean premierJoueur;

    public Player(PApplet parent, PVector elbow, PVector center, float angle, int color) {
        this(parent, elbow, center, angle, color, false);

    }
    public Player(PApplet parent, PVector elbow, PVector center, float angle, int color, boolean premier) {
        this.parent = parent;
        this.elbow = elbow;
        this.center = center;
        this.angle = angle;
        this.size = center.z;
        this.color = color;

        this.score = 0;

        this.premierJoueur = premier;
    }

    public void updatePosition(PVector elbow, PVector center, float angle, int color) {
        float lerp = 0.5f;
        this.elbow = new PVector(parent.lerp(this.elbow.x, elbow.x, lerp), parent.lerp(this.elbow.y, elbow.y, lerp), parent.lerp(this.elbow.z, elbow.z, lerp));
        this.center = new PVector(parent.lerp(this.center.x, center.x, lerp), parent.lerp(this.center.y, center.y, lerp), parent.lerp(this.center.z, center.z, lerp));
        this.angle = parent.lerp(this.angle, angle, lerp);
        this.size = this.center.z;
        this.color = color;
    }

    void display() {
        //if (!this.premierJoueur)
        //    System.out.println("Affichage 2e joueur");

        parent.translate(0, 0, 0);
        parent.stroke(color);
        parent.strokeWeight(10);
        parent.line(elbow.x, elbow.y, center.x, center.y);

        parent.translate(center.x, center.y);
        parent.rotateX(angle);
        parent.fill(color);
        parent.stroke(color);
        parent.box(130/size, 160/size, 1);

        //parent.point(center.x, center.y);
    }

    public PVector getDirection() {
        PVector direction = new PVector((float)Math.cos(angle), (float)Math.sin(angle));

        return direction;
    }
}
