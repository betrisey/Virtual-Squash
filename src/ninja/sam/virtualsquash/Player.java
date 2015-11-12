/*
Virtual Squash 2 - 2015 - Samuel Bétrisey
Classe Player
 - Stocke les position du joueurs et son score
 - Affiche la raquette
 - Calcule la direction de la frappe
*/

package ninja.sam.virtualsquash;

import processing.core.PApplet;
import processing.core.PVector;

public class Player {
    private static int WIDTH = 100; // Largeur de la raquette, elle sera divisée par la distance du joueur pour un effet de perspective
    private static int HEIGHT = 200;// Longueur de la raquette, elle sera divisée par la distance du joueur pour un effet de perspective

    private PApplet parent; // Classe main, utilisée pour dessiner dans la classe parent

    public PVector elbow, center, direction;
    public float distance, angleX, angleZ, width, height;
    public int score, color;

    public int rightHanded = -1; // -1 pas encore détecté, 0 gaucher, 1 droitier
    private int leftCount, rightCount; // Nombre de fois que la main gauche/droite a été utilisée

    private PVector[] lastPositions;   // 15 dernières positions de la main
                                       // Utilisé pour calculer la direction de la frappe

    public Player(PApplet parent, PVector elbow, PVector center, float angleX, float angleZ, int color) {
        this.parent = parent;
        this.elbow = elbow;
        this.center = center;
        this.angleX = angleX;
        this.angleZ = angleZ;
        this.color = color;

        // Longueur et largeur divisées par la distance de la main pour un effet de perspective
        this.width = WIDTH/center.z;
        this.height = HEIGHT/center.z;

        this.direction = new PVector(0,0,0);
        // Initialisation des dernières positions
        lastPositions = new PVector[15];
        for (int i=0; i<lastPositions.length; i++)
            lastPositions[i] = center;

        this.score = 0;
    }

    public void updatePosition(PVector elbow, PVector center, float angleX, float angleZ, int color) {
        // Mise à jour de la position de la main et du coude

        // Calcul de la direction de la frappe avec la position actuelle et celle il y a 0.5 sec
        this.direction = new PVector(center.x-lastPositions[14].x, center.y-lastPositions[14].y, -(center.z-lastPositions[14].z)*2000); //Mouvement de la main lors des 15 dernières frames (environ 0.5s)
        // Décalage du tableau pour garder les 15 positions les plus récentes
        shiftArray(lastPositions);
        lastPositions[0] = center;

        // Utilisation d'un lerp pour des mouvements plus fluides
        float lerp = 0.7f;
        this.elbow = new PVector(PApplet.lerp(this.elbow.x, elbow.x, lerp), PApplet.lerp(this.elbow.y, elbow.y, lerp), PApplet.lerp(this.elbow.z, elbow.z, lerp));
        this.center = new PVector(PApplet.lerp(this.center.x, center.x, lerp), PApplet.lerp(this.center.y, center.y, lerp), PApplet.lerp(this.center.z, center.z, lerp));
        this.angleX = PApplet.lerp(this.angleX, angleX, lerp);
        this.angleZ = PApplet.lerp(this.angleZ, angleZ, lerp);
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
        return direction;
    }

    public void setHand(boolean rightHanded) {
        // On reçoit true si la main droite a été utilisée

        if (rightHanded)
            rightCount++;
        else
            leftCount++;

        // Après 100 frames (3-4 sec) on déterminer définitivement si le joueur est gaucher ou droitier
        if (leftCount+rightCount >= 100) {
            if (rightCount > leftCount)
                this.rightHanded = 1;
            else
                this.rightHanded = 0;
        }
    }

    private void shiftArray(PVector[] array) {
        System.arraycopy(array, 0, array, 1, array.length - 1);
    }
}
