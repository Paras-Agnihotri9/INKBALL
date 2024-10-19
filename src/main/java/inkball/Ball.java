package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;


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
    public boolean captured;
    private boolean scoreUpdated = false; // New flag to check if score is already updated
    public boolean added = false;
    public static boolean isCaptureSuccessfulFlag = false;

    // Variables for shrinking animation
    public boolean shrinking = false;
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
        this.captured = false;
        this.currentSize = originalSize; // Start at the original size
    }
    public String getBallId(){
        return color;
    }

    public void update( ) {
        if (!captured) {
            // Move the ball by velocity
            x += velocityX;
            y += velocityY;

            // Handle shrinking if the shrinking flag is set
            if (shrinking) {
                currentSize -= shrinkRate; // Decrease size by shrinkRate

                // Ensure currentSize doesn't go below targetSize
                if (currentSize <= targetSize) {
                    currentSize = targetSize; // Clamp the size
                    shrinking = false; // Stop shrinking
                    setCaptured(true); 
                }
            }

            // Check attraction to holes
        }
    }

     public void display(PApplet app) {
        if (!captured) { // Only display if not captured
            int ballIndex = getColorIndex(color);
            // Display the ball image at the current size (scale the image)
            app.image(balls[ballIndex], x, y, currentSize, currentSize);
        }
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
        targetSize = 12; // Set target size for shrinking
        shrinking = true; // Start the shrinking process
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

    public int getRadius() {
        return 12;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured; // or whatever logic you want for capturing
    }

    public void setCurrentSize(float newSize) {
        this.currentSize = newSize;
    }

    private float constrain(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public float getCurrentSize() {
        return currentSize; // Getter for current size
    }

    public float getTargetSize() {
        return targetSize;
    }
    public void attractToHole(Hole hole) {
        // Calculate the center of the hole
        float holeCenterX = hole.getCenterX();
        float holeCenterY = hole.getCenterY();

        // Calculate the distance vector to the center of the hole
        PVector toHole = new PVector(holeCenterX - (x + currentSize / 2), holeCenterY - (y + currentSize / 2));
        float distance = toHole.mag(); // Get the distance to the center of the hole
        toHole.normalize(); // Normalize to get direction

        // Check if the ball is within a certain distance from the center of the hole
        if (distance < 32 + getRadius()) { // 32 pixels + ball radius
            float attractionForce = 0.005f * distance; // 0.5% of distance

            // Apply the attraction force to the ball's velocity
            applyForce(toHole.x * attractionForce, toHole.y * attractionForce);

            // Start shrinking if the ball is within the attraction radius and not already shrinking
            if (!shrinking) {
                startShrinking();
            }

            // Reduce size based on proximity to the hole
            currentSize = originalSize * (distance / 32);
            currentSize = constrain(currentSize, targetSize, originalSize); 

            // Check if the ball is captured by the hole
            if (captured == true) {
                String holeType = String.valueOf(hole.type); // Get hole type as a String
                isCaptureSuccessfulFlag = isCaptureSuccessful(holeType);
                // Update score based on capture success
                updateScore(isCaptureSuccessfulFlag, holeType);
                
            }
        } 
    }

    private boolean isCaptureSuccessful(String holeType) {
        // Grey ball or grey hole
        if (color.equals("grey") || holeType.equals("0")) {
            return true; // Always successful for grey
        }
        // Check if the ball's color matches the hole's type
        return color.equals(getColorFromHoleType(holeType));
    }

    private String getColorFromHoleType(String holeType) {
        switch (holeType) {
            case "0": return "grey";
            case "1": return "orange";
            case "2": return "blue";
            case "3": return "green";
            case "4": return "yellow";
            default: return null; // Invalid type
        }
    }

    private void updateScore(boolean isCaptureSuccessful, String holeType) {
    if (!scoreUpdated) {
        System.out.println("Capture Successful: " + isCaptureSuccessful + ", Hole Type: " + holeType);
        if (isCaptureSuccessful) {
            // Successful capture
            float scoreIncrease = App.scoreIncreaseModifier * 3;
            App.score += scoreIncrease; // Increase score
            System.out.println("Score increased by: " + scoreIncrease + ", New Score: " + App.score);
        } else {
            // Unsuccessful capture
            float scoreDecrease = App.scoreDecreaseModifier;
            App.score -= scoreDecrease; // Decrease score
            System.out.println("Score decreased by: " + scoreDecrease + ", New Score: " + App.score);
        }
        scoreUpdated = true; // Mark the score as updated to prevent further increments
    }
}



    
}