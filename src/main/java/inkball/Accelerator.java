package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Accelerator {
    private int x, y;
    private String type; 
    private PImage image;

    // Constructor to initialize position, type, and image
    public Accelerator(int x, int y, String type, PImage image) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.image = image;
    }

    /**
     * Function to display the accelerator on the game screen
     * @param app app the PApplet instance to draw the accelerator
     * */ 
    public void display(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLHEIGHT, App.CELLSIZE, App.CELLHEIGHT);
    }

    /** 
     * Function to accelerate the ball when it comes over the accelerator
     * @param ball the ball instance to check overlapping
     */ 
    public void applyAcceleration(Ball ball) {
        if (isOverlapping(ball)) {
            switch (type) {
                case "top":
                    ball.applyForce(0, -0.5f);  // Increase upward velocity
                    break;
                case "bottom":
                    ball.applyForce(0, 0.5f);  // Increase downward velocity
                    break;
                case "left":
                    ball.applyForce(-0.5f, 0);  // Increase leftward velocity
                    break;
                case "right":
                    ball.applyForce(0.5f, 0);  // Increase rightward velocity
                    break;
            }
        }
    }

    /**
     * Check if the ball overlaps with the accelerator
     * @param ball chceks overlapping using AABB logic
     * @return true if the ball is overlapping, false if the ball isnt.
     */
    private boolean isOverlapping(Ball ball) {
        float ballCenterX = ball.getX() + ball.getRadius();
        float ballCenterY = ball.getY() + ball.getRadius();
        int ballRadius = ball.getRadius();

        // Accelerator boundaries
        int accelLeft = x * App.CELLSIZE;
        int accelRight = accelLeft + App.CELLSIZE;
        int accelTop = y * App.CELLHEIGHT;
        int accelBottom = accelTop + App.CELLHEIGHT;

        // Check if ball is overlapping with the accelerator
        return ballCenterX + ballRadius > accelLeft && ballCenterX - ballRadius < accelRight &&
               ballCenterY + ballRadius > accelTop && ballCenterY - ballRadius < accelBottom;
    }
}
