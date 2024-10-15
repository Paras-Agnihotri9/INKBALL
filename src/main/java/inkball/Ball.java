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
    private int radius = 12;
    private boolean hasCollided;
    
    public Ball(float x, float y, String ballColor, PImage[] balls) {
        this.x = x;
        this.y = y;
        this.color = ballColor;
        this.balls = balls; // Store the reference
        this.velocityX = VELOCITY_OPTIONS[random.nextInt(2)];
        this.velocityY = VELOCITY_OPTIONS[random.nextInt(2)];
        this.hasCollided = false;
    }

    public void update() {
        x += velocityX;
        y += velocityY;
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

    public float getVelocityX(){
        return velocityX;
    }
    public float getVelocityY(){
        return velocityY;
    }

    public void setVelocity(float newVelocityX, float newVelocityY){
    this.velocityX = newVelocityX;  // Update the actual velocity
    this.velocityY = newVelocityY;  // Update the actual velocity
    }

   
    public int getRadius() {
        return 12; // Fixed radius
    }


    public void reverseHorizontalDirection() {
        velocityX *= -1;  // Reverse X velocity (horizontal direction)
    }

    public void reverseVerticalDirection() {
        velocityY *= -1;  // Reverse Y velocity (vertical direction)
    }

    public void checkPlayerLineCollision(PlayerLine line) {
        if (line.intersects(x, y)) {
            // Get the line's normal vector and reflect the ball
            float[] normal = getNormal(line);
            reflectBall(normal[0], normal[1]);
        }
    }

    // Reflect the ball based on the normal vector of the line
    private void reflectBall(float normalX, float normalY) {
        // Calculate dot product between velocity and normal
        float dotProduct = (velocityX * normalX + velocityY * normalY);

        // Reflect velocity vector using the formula u = v - 2(v ⋅ n)n
        velocityX = velocityX - 2 * dotProduct * normalX;
        velocityY = velocityY - 2 * dotProduct * normalY;
    }

    // Find the normal of the line segment (based on your steps)
    private float[] getNormal(PlayerLine line) {
        // For simplicity, return an example normal vector; modify this to calculate real normal
        return new float[]{0, 1}; // Replace this with actual normal calculation
    }



    public void setColor(String new_color){
        color = new_color;
    }
    
    public void setX(float xCordinate){
        this.x = xCordinate;
    }

    public void setY(float yCordinate){
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
}