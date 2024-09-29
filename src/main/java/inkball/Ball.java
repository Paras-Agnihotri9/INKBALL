package inkball;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.Random;

public class Ball {
    private static final Random random = new Random();
    private static final int VELOCITY_OPTIONS[] = {-2, 2};

    private float x; // X position of the ball
    private float y; // Y position of the ball
    private String color; // Color of the ball based on its ID
    private float velocityX; // Velocity in the x direction
    private float velocityY; // Velocity in the y direction
    private PImage image; // Image of the ball

    public Ball(float x, float y, String ballId, PImage[] ballImages) {
        this.x = x;
        this.y = y;
        this.color = ballId;
        this.image = ballImages[Integer.parseInt(ballId.substring(1))]; // Set the correct image based on the ball ID
        this.velocityX = VELOCITY_OPTIONS[random.nextInt(2)];
        this.velocityY = VELOCITY_OPTIONS[random.nextInt(2)];
    }


    public void update() {
        x += velocityX;
        y += velocityY;

        // Check for wall collisions here (to be implemented)
    }

    public void display(PApplet applet) {
        applet.image(image, x, y);
    }

    // Getters for position if needed
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getColor() {
        return color;
    }

    // Static method to create a ball from configuration
    public static Ball spawnBall(String ballId, float x, float y, PImage[] ballImages) {
        return new Ball(x, y, ballId, ballImages);
    }

    public float getVelocityX(){
        return velocityX;
    }

    public float getVelocityY(){
        return velocityY;
    }

    

}