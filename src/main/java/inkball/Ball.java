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
    private int collisionCooldown = 0;

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

   
    public int getRadius() {
        return 12; // Fixed radius
    }

    public void handleWallCollision(char[][] board, int boardWidth, int boardHeight) {
        
        // Calculate the ball's edges
        float leftEdge = x;
        float rightEdge = x + radius * 2;
        float topEdge = y;
        float bottomEdge = y + radius * 2;

        // Handle screen edge collisions
        if (leftEdge < 0 || rightEdge > App.WIDTH) {
            // Collision with left or right screen edge (horizontal)
            velocityX *= -1;  // Reverse X velocity only
        }

        if (topEdge < 0 || bottomEdge > App.HEIGHT) {
            // Collision with top or bottom screen edge (vertical)
            velocityY *= -1;  // Reverse Y velocity only
        }

        // Handle board tile collisions
        int centerX = (int) ((x + radius) / App.CELLSIZE);  // Center X in grid units
        int centerY = (int) ((y + radius) / App.CELLHEIGHT);  // Center Y in grid units

        // Ensure we are within bounds of the board
        if (centerX >= 0 && centerX < boardWidth && centerY >= 0 && centerY < boardHeight) {
            char tile = board[centerX][centerY];

            // Check if the current tile is a wall
            if (tile == 'X' || (tile >= '1' && tile <= '4')) {
                // Get the relative position of the ball to the tile
                float distanceX = (x + radius) % App.CELLSIZE;  // Distance to the left/right side of the cell
                float distanceY = (y + radius) % App.CELLHEIGHT;  // Distance to the top/bottom of the cell

                // Determine whether to reverse X or Y velocity
                if (distanceX < radius || distanceX > App.CELLSIZE - radius) {
                    // Ball is hitting a vertical edge of the tile, reverse X velocity
                    velocityX *= -1;
                } else if (distanceY < radius || distanceY > App.CELLHEIGHT - radius) {
                    // Ball is hitting a horizontal edge of the tile, reverse Y velocity
                    velocityY *= -1;
                }

                // Change color if it's a colored wall
                if (tile != 'X') {
                    color = getWallColor(tile);
                }
            }
        }
    }


    // Helper method to get the wall color based on tile type
    private String getWallColor(char wallTile) {
        switch (wallTile) {
            case 'X': return "grey";
            case '1': return "orange";
            case '2': return "blue";
            case '3': return "green";
            case '4': return "yellow";
            default: return "grey";
        }
    }
}