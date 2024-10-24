 package inkball;

public class Wall {
    private int x, y;
    private char wallType;
    String newColor;

    public Wall(int x, int y, char wallType) {
        this.x = x;
        this.y = y;
        this.wallType = wallType;
    }

    /**
     * Returns the type of wall
     * @return WallType is returned
     */
    public char getWallType() {
        return wallType;
    }

    /**
     * returns the X coordinate of the Hole
     * @return X is returned
     */
    public int getX() {
        return x;
    }

    /**
     * returns the Y coordinate of the Hole
     * @return Y  is returned
     */
    public int getY() {
        return y;
    }

    /**
     * Calculate the ball's center X coordinate
     * @param ball used in order to calculate the centre X coordinate
     * @return CentreOfTheBallX is returned
     */
    private float calculateBallCenterX(Ball ball) {
        return ball.getX() + ball.getRadius();
    }

    /**
     * Calculate the ball's center Y coordinate
     * @param ball used in order to calculate the centre X coordinate
     * @return CentreOfTheBallY is returned
     */
    private float calculateBallCenterY(Ball ball) {
        return ball.getY() + ball.getRadius();
    }

    /**
     * Collision detection method, checks for overlapping with the ball
     * @param ball used to check if there is any overlapping with any of the walls
     * @return true if there is an overlap, false if there is none
     */
    public boolean isOverlapping(Ball ball) {
        float ballCenterX = calculateBallCenterX(ball);
        float ballCenterY = calculateBallCenterY(ball);
        int ballRadius = ball.getRadius();

        // Wall coordinates
        int wallLeft = x * App.CELLSIZE;
        int wallRight = wallLeft + App.CELLSIZE;
        int wallTop = y * App.CELLHEIGHT;
        int wallBottom = wallTop + App.CELLHEIGHT;

        // Check for overlap (AABB collision detection)
        boolean overlap = ballCenterX + ballRadius > wallLeft && ballCenterX - ballRadius < wallRight &&
                          ballCenterY + ballRadius > wallTop && ballCenterY - ballRadius < wallBottom;

        return overlap;
    }

    /**
     * Main function to handle ball-wall collision and change the position/velocity of the ball accordingly
     * @param ball take in ball to determine collision
     */
    public void handleCollision(Ball ball) {
        if (!isOverlapping(ball)) return;  // No collision detected

        float ballCenterX = calculateBallCenterX(ball);  // Get ball's center
        float ballCenterY = calculateBallCenterY(ball);
        int ballRadius = ball.getRadius();

        // Wall boundaries (assumed to be rectangular grid cells)
        int wallLeft = x * App.CELLSIZE;
        int wallRight = wallLeft + App.CELLSIZE;
        int wallTop = y * App.CELLHEIGHT;
        int wallBottom = wallTop + App.CELLHEIGHT;

        // Calculate distances from ball edge to the wall edges
        float distToLeft = (ballCenterX - ballRadius) - wallLeft;
        float distToRight = wallRight - (ballCenterX + ballRadius);
        float distToTop = (ballCenterY - ballRadius) - wallTop;
        float distToBottom = wallBottom - (ballCenterY + ballRadius);

        // Find the closest side by checking the smallest distance
        float minDistance = Math.min(Math.min(distToLeft, distToRight), Math.min(distToTop, distToBottom));

        // Reflect the ball based on the closest side and adjust its position
        if (minDistance == distToLeft) {
            // Ball hit the left side of the wall, reverse X direction
            ball.reverseHorizontalDirection();
            ball.setX(wallLeft - 2 * ballRadius);  // Move ball just outside the left wall boundary
        } else if (minDistance == distToRight) {
            // Ball hit the right side of the wall, reverse X direction
            ball.reverseHorizontalDirection();
            ball.setX(wallRight);  // Move ball just outside the right wall boundary
        } else if (minDistance == distToTop) {
            // Ball hit the top side of the wall, reverse Y direction
            ball.reverseVerticalDirection();
            ball.setY(wallTop - 2 * ballRadius);  // Move ball just outside the top wall boundary
        } else if (minDistance == distToBottom) {
            // Ball hit the bottom side of the wall, reverse Y direction
            ball.reverseVerticalDirection();
            ball.setY(wallBottom);  // Move ball just outside the bottom wall boundary
        }

        // Handle color change for colored walls
        if (Character.isDigit(wallType)) {
            String newColor = getWallColor(wallType);
            ball.setColor(newColor);
        }
    }
/**
 * displays the color of the wall based on the type
 * @param wallTile wall type to determine color
 * @return determined color
 */
    public String getWallColor(char wallTile) {
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
