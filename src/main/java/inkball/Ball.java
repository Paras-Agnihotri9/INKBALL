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
    public float velocityX; // Velocity in the x direction
    public float velocityY; // Velocity in the y direction
    private PImage[] balls; // Reference to ball images
    private int originalSize = 24; // Original size of the ball (width and height of the image)
    private float currentSize; // Current size of the ball for scaling
    private boolean hasCollided;
    public static int score = 0;
    public boolean captured;

    // Variables for shrinking animation
    private boolean shrinking = false;
    private float targetSize = 12; // Target smaller size for shrinking
    private float shrinkRate = 0.5f; // Rate at which the ball will shrink

    public Ball(float x, float y, String ballColor, PImage[] balls) {
        this.x = x;
        this.y = y;
        this.color = ballColor;
        this.balls = balls; // Store the reference
        this.velocityX = VELOCITY_OPTIONS[random.nextInt(2)];
        this.velocityY = VELOCITY_OPTIONS[random.nextInt(2)];
        this.hasCollided = false;
        this.captured = true;
        this.currentSize = originalSize; // Start at the original size
    }

    public void update() {
        // Move the ball by velocity
        x += velocityX;
        y += velocityY;

        // Handle shrinking logic if the ball is in shrink mode
        if (shrinking) {
            shrinkBall();
        }
    }

    public void display(PApplet app) {
        int ballIndex = getColorIndex(color);
        // Display the ball image at the current size (scale the image)
        app.image(balls[ballIndex], x, y, currentSize, currentSize);
    }

    private int getColorIndex(String color) {
        switch (color) {
            case "grey":
                return 0;
            case "orange":
                return 1;
            case "blue":
                return 2;
            case "green":
                return 3;
            case "yellow":
                return 4;
            default:
                return 0; // Default to grey if not found
        }
    }

    public void startShrinking() {
        shrinking = true;
    }

    private void shrinkBall() {
        if (currentSize > targetSize) {
            currentSize -= shrinkRate; // Gradually reduce the size
            if (currentSize < targetSize) {
                currentSize = targetSize; // Ensure it doesn't shrink below the target size
                shrinking = false; // Stop shrinking once the target size is reached
            }
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocity(float newVelocityX, float newVelocityY) {
        this.velocityX = newVelocityX;  // Update the actual velocity
        this.velocityY = newVelocityY;  // Update the actual velocity
        System.out.println("VelocityX" + velocityX);
        System.out.println("VelocityY" + velocityY);
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public void reverseHorizontalDirection() {
        velocityX *= -1;  // Reverse X velocity (horizontal direction)
    }

    public void reverseVerticalDirection() {
        velocityY *= -1;  // Reverse Y velocity (vertical direction)
    }

    public void setColor(String new_color) {
        color = new_color;
    }

    public void setX(float xCordinate) {
        this.x = xCordinate;
    }

    public void setY(float yCordinate) {
        this.y = yCordinate;
    }

    public void resetCollision() {
        hasCollided = false; // Reset for the next frame
    }

    public boolean hasCollided() {
        return hasCollided; // Getter for collision status
    }

    public void setHasCollided(boolean hasCollided) {
        this.hasCollided = hasCollided; // Setter for collision status
    }

    public void applyForce(float forceX, float forceY) {
        this.velocityX += forceX;
        this.velocityY += forceY;
    }

    public String getColor() {
        return color;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured; // or whatever logic you want for capturing
    }

    public int getRadius() {
        return 12; // Fixed radius
    }

    public void setSize(){
        System.out.println("ahahahh");
    }

}
