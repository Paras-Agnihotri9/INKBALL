package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import java.util.List;


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
    private boolean outOfBounds =false;
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

    /**
     * updates the position of the ball according to its velocity, also responsible for shrinking the ball when shriking is set to true.
     */
    public void update( ) {
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
                }
            }
    }

    /**
     * display function responsible to display the image of the ball at the given co-ordinates of the ball
     * @param app the PApplet instance to draw the ball
     */

    public void display(PApplet app) {
        if (!captured) { // Only display if not captured
            int ballIndex = getColorIndex(color);
            // Display the ball image at the current size (scale the image)
            app.image(balls[ballIndex], x, y, currentSize, currentSize);
        }
    }

    /**
     * returns the color index based on the string color.
     * @param color color of the ball
     * @return color as a number
     */
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

    /**
     * returns the X coordinate.
     * @return x the value of the x coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * returns the Y coordinate.
     * @return Y the value of the y coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the velocity of the ball
     * @param newVelocityX takes in the new velocity to set it
     * @param newVelocityY takes in the new velocity to set it
     */
    public void setVelocity(float newVelocityX, float newVelocityY) {
        this.velocityX = newVelocityX;  
        this.velocityY = newVelocityY;
    }

    /**
     * Reverses Horizontal direction
     */
    public void reverseHorizontalDirection() {
        velocityX *= -1;
    }

    /**
     * Reverses vertical direction
     */
    public void reverseVerticalDirection() {
        velocityY *= -1;
    }

    /**
     * sets color
     * @param new_color takes new color to set new color
     */
    public void setColor(String new_color) {
        color = new_color;
    }

    /**
     * sets X coordinate
     * @param xCordinate takes in the xcordinate to set it
     */
    public void setX(float xCordinate) {
        this.x = xCordinate;
    }

    /**
     * sets Y coordinate
     * @param yCordinate takes in the xcordinate to set it
     */
    public void setY(float yCordinate) {
        this.y = yCordinate;
    }

    /**
     * returns the Hascollided boolean
     * @return true if there has been a collision, false if there hasnt
     */
    public boolean hasCollided() {
        return hasCollided; // Getter for collision status
    }

    /**
     * sets the has collided boolean
     * @param hasCollided takes in hasCollided in order to set it
     */
    public void setHasCollided(boolean hasCollided) {
        this.hasCollided = hasCollided; // Setter for collision status
    }

    /**
     * resets the collision boolean
     */
    public void resetCollision() {
        hasCollided = false; // Reset for the next frame
    }


    /**
     * Changes the velocity progressively
     * @param forceX takes in the forceX in order to increase the X velocity
     * @param forceY takes in the forceY in order to increase the Y velocity
     */
    public void applyForce(float forceX, float forceY) {
        this.velocityX += forceX;
        this.velocityY += forceY;
    }

    /**
     *  Returns the color of the ball
     * @return color of the ball
     */
    public String getColor() {
        return color;
    }

    /**
     *  Returns the radius of the ball
     * @return the radius of the ball
     */
    public int getRadius() {
        return 12;
    }

    /**
     * sets the captured boolean
     * @param captured sets the captured boolean
     */
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    /**
     * Sets the size of the ball
     * @param newSize sets the newSize value
     */
    public void setCurrentSize(float newSize) {
        this.currentSize = newSize;
    }

    /**
     * return the constraint between 3 numbers
     * @param value takes in a value 
     * @param min takes in the minimum
     * @param max takes in the maximum
     * @return the constraint is returned 
     */
    private float constrain(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * returns the current size of the ball
     * @return currentSize is returned
     */
    public float getCurrentSize() {
        return currentSize; // Getter for current size
    }

    /**
     * returns the target size of the ball
     * @return targetSize is returned
     */
    public float getTargetSize() {
        return targetSize;
    }

    /**
     * Responsible for attracting the ball towards the closest hole when the ball is within 32px of the board.
     * @param holes a list of holes is passed so that the closest one can be determined
     * 
     */

    public void attractToHoles(List<Hole> holes) {
        Hole closestHole = null;
        float closestDistance = Float.MAX_VALUE;

        // Find the closest hole
        for (Hole hole : holes) {
            float holeCenterX = hole.getCenterX();
            float holeCenterY = hole.getCenterY();

            // Calculate distance from the ball's center to the hole's center
            PVector toHole = new PVector(holeCenterX - (x + currentSize / 2), holeCenterY - (y + currentSize / 2));
            float distance = toHole.mag(); // Get the distance to the center of the hole

            // Update if this hole is closer
            if (distance < closestDistance) {
                closestDistance = distance;
                closestHole = hole;
            }
        }

        if (closestHole != null && closestDistance < 32 + getRadius()) {
            // The ball is within attraction range of the closest hole
            PVector toHole = new PVector(
                    closestHole.getCenterX() - (x + currentSize / 2),
                    closestHole.getCenterY() - (y + currentSize / 2)
            );
            toHole.normalize(); // Normalize direction to the hole

            // Apply attraction force based on proximity
            float attractionForce = 0.005f * closestDistance; // 0.5% of distance
            applyForce(toHole.x * attractionForce, toHole.y * attractionForce);

            // Start shrinking if not already shrinking
            if (!shrinking && !outOfBounds) {
                shrinking = true;
            }

            // Reduce size based on proximity to the closest hole
            currentSize = originalSize * (closestDistance / 32);
            currentSize = constrain(currentSize, targetSize, originalSize);

            // Check if the ball is captured by the hole
            if (closestDistance < getRadius()) {
                setCaptured(true);
                String holeType = String.valueOf(closestHole.type); // Get hole type as a String
                isCaptureSuccessfulFlag = isCaptureSuccessful(holeType);
                updateScore(isCaptureSuccessfulFlag, holeType); // Update score based on capture success
            }
        } else {
            // Ball is no longer in range of any hole, reset size
            resetToOriginalSize();
            outOfBounds = true; // Set flag indicating the ball is out of bounds
        }
    }


    /**
     * resets the ball to its original size
     */
    public void resetToOriginalSize() {
        // Only reset the ball size if it's not shrinking and hasn't been captured
        if (!captured) {
            currentSize = originalSize;  // Restore the original size
            shrinking = false;  // Stop any shrinking process
            outOfBounds = true; // Keep outOfBounds flag true
        }
    }

    /**
     * return whether the capture was succesful or not
     * @param holeType uses the type of hole to determine whether it has been a succesful capture or not
     * @return captureSuccessful if the capture has been succesful
     */
    private boolean isCaptureSuccessful(String holeType) {
            // Grey ball or grey hole
        if (color.equals("grey") || holeType.equals("0")) {
                return true; // Always successful for grey
            }
            // Check if the ball's color matches the hole's type
            return color.equals(getColorFromHoleType(holeType));
        }

    /**
     * returns the color of the hole based on its type
     * @return color
     */
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

    /**
     * updates the score according to the level and score modifier, reduces the score when the capture is unsuccesful and increases the score when the capture is succesful.
     * @param isCaptureSuccessful takes in the return value from the function to determine whether to increase/decrease the score.
     * @param holeType used to know which hole the ball has been capture into
     */
    public void updateScore(boolean isCaptureSuccessful, String holeType) {
        if (!scoreUpdated) {
            System.out.println("Capture Successful: " + isCaptureSuccessful + ", Hole Type: " + holeType);
            if (isCaptureSuccessful) {
                // Successful capture
                float scoreIncrease = App.scoreIncreaseModifier * (App.currentLevelIndex+1);
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
