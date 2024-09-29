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
    private PImage[] balls; // Reference to ball images

    public Ball(float x, float y, String ballColor, PImage[] balls) {
        this.x = x;
        this.y = y;
        this.color = ballColor;
        this.balls = balls; // Store the reference
        this.velocityX = VELOCITY_OPTIONS[random.nextInt(2)];
        this.velocityY = VELOCITY_OPTIONS[random.nextInt(2)];
    }

    public void update() {
        x += velocityX;
        y += velocityY;

        // Implement boundary checks to reverse direction if the ball hits the walls
        if (x < 0 || x > App.WIDTH - App.CELLSIZE) {
            velocityX *= -1; // Reverse x direction
        }
        if (y < 0 || y > App.HEIGHT - App.CELLHEIGHT) {
            velocityY *= -1; // Reverse y direction
        }
    }
    
    public void display(PApplet app) {
        int ballIndex = getColorIndex(color);
        app.image(balls[ballIndex], x, y);
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
