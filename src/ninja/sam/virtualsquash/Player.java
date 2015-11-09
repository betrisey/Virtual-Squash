package ninja.sam.virtualsquash;

import processing.core.PApplet;
import processing.core.PVector;

public class Player {
    private PApplet parent;

    public PVector elbow, center, lastCenter, direction;
    public float distance, angleX, angleZ, width, height;
    public int score, color;

    public int rightHanded = -1; //-1 pas encore détecté, 0 gaucher, 1 droitier
    private int leftCount, rightCount;

    private static int WIDTH = 100;
    private static int HEIGHT = 200;

    public Player(PApplet parent, PVector elbow, PVector center, float angleX, float angleZ, int color) {
        this.parent = parent;
        this.elbow = elbow;
        this.center = center;
        this.angleX = angleX;
        this.angleZ = angleZ;
        this.color = color;
        this.width = WIDTH/center.z;
        this.height = HEIGHT/center.z;
        this.direction = new PVector(0,0,0);
        this.lastCenter = new PVector(0,0,0);

        this.score = 0;
    }

    public void updatePosition(PVector elbow, PVector center, float angleX, float angleZ, int color) {
        this.direction.lerp(new PVector(center.x-lastCenter.x, center.y-lastCenter.y, (center.z-lastCenter.z)), 1.0f/10);//Mouvement de la main lors des 10 derni�res frames (environ 0.3s)

        float lerp = 0.5f;
        this.lastCenter = center;
        this.elbow = new PVector(parent.lerp(this.elbow.x, elbow.x, lerp), parent.lerp(this.elbow.y, elbow.y, lerp), parent.lerp(this.elbow.z, elbow.z, lerp));
        this.center = new PVector(parent.lerp(this.center.x, center.x, lerp), parent.lerp(this.center.y, center.y, lerp), parent.lerp(this.center.z, center.z, lerp));
        this.angleX = parent.lerp(this.angleX, angleX, lerp);
        this.angleZ = parent.lerp(this.angleZ, angleZ, lerp);
        this.width = WIDTH/center.z;
        this.height = HEIGHT/center.z;
        this.distance = this.center.z;
        this.color = color;
    }

    public void display() {
        // Affichage du manche de la raquette
        parent.stroke(color);
        parent.strokeWeight(10);
        parent.line(elbow.x, elbow.y, center.x, center.y);

        // Affichage de la raquette
        parent.pushMatrix();
        parent.translate(center.x, center.y);
        parent.rotateX(angleX);
        parent.rotateZ(angleZ);
        parent.fill(color);
        parent.noStroke();
        parent.box(width, height, 15);
        parent.popMatrix();
    }

    public PVector getDirection() {
        return direction.normalize();
    }

    public void setHand(boolean rightHanded) {
        if (rightHanded)
            rightCount++;
        else
            leftCount++;

        if (leftCount+rightCount >= 100) {
            if (rightCount > leftCount)
                this.rightHanded = 1;
            else
                this.rightHanded = 0;
        }
    }
}
