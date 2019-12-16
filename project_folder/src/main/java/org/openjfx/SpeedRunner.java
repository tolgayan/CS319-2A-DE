package org.openjfx;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

//enemyType1

public class SpeedRunner extends Enemy {

    SpeedRunner(double width, double height, String assetLocation) {
        super(width / 30, height / 10, "speedRunner");
        this.width = width / 30;
        this.height = height / 10;
        super.initBody(assetLocation, width, height);
    }

    public void moveSpeedRunner(GameComponentFactory GCF, Pane gameRoot, Player player, boolean left) {

        // TODO: Why 10000? Explain with comments or make it a constant variable
        double random = Math.random() * 10000; // random for chance based updates

        if (delay) { // delay: a boolean value to delay shoots
            if (random < 9000) { // %1.5 chance TODO: Constant problem for 150

                // TODO: explain or convert to a constant: 38.4 and -1
                boolean isObjectInScene = getX() <= width * 38.4 - gameRoot.getTranslateX() && getX() > gameRoot.getTranslateX() * -1;

                if (isObjectInScene) { // if the enemy is in the view of the player
                    String bulletType = "laserBullet";
                    initBullet(GCF, bulletType);

                    delay = false; // make delay false
                    delayTimer = 50; // start delay timer TODO: Constant problem for 500
                }
            }
        } else { // if delay is on
            if (delayTimer == 0) // if delay timer finished
                delay = true; // make delay true
            delayTimer -= 5; // decrease delay timer TODO: Why? Could you explain
        }

        // get new coordinates and speed for the next frame
        int[] moveValues = getMoveValues(random);

        directionX = moveValues[0];
        directionY = moveValues[1];
        speed_x = moveValues[2] ;
        speed_y = moveValues[3] ;

        moveX(directionX, speed_x * 5); // move X with given inputs
        moveY(directionY, speed_y ); // move Y with given inputs

        //updates the space ships so they loop around map
        loopAroundTheMap(GCF.width, player, left);

        // Actions when collision
        for (Shape hitBox : hitBoxes) {
            if (hitBox instanceof ComponentHitBoxCircle) {
                ComponentHitBoxCircle temp = ((ComponentHitBoxCircle) hitBox);
                if (temp.isDead())
                    dead = true;
            } else if (hitBox instanceof ComponentHitBoxRectangle) {
                ComponentHitBoxRectangle temp = ((ComponentHitBoxRectangle) hitBox);
                if (temp.isDead()) {
                    dead = true;
                }
            }
        }
    }
}