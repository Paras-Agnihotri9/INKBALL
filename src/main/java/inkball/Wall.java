 package inkball;

public class Wall {
    private int x, y;
    private char wallType; // X for regular, '1' to '4' for colored walls
    String newColor;

    public Wall(int x, int y, char wallType) {
        this.x = x;
        this.y = y;
        this.wallType = wallType;
    }

    public char getWallType() {
        return wallType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Collision detection method
    private int collisionBuffer = 0; // Add this as a field in the Wall class

public void checkCollision(Ball ball) {
    float ballX = ball.getX();
    float ballY = ball.getY();
    int ballRadius = ball.getRadius();

    // Wall coordinates
    int wallLeft = x * App.CELLSIZE;
    int wallRight = wallLeft + App.CELLSIZE;
    int wallTop = y * App.CELLHEIGHT;
    int wallBottom = wallTop + App.CELLHEIGHT;

    boolean collisionDetected = false;

    // Check horizontal collision
    if (collisionBuffer % Math.abs(ball.velocityX) == 0) {
        // Check left side collision
        if (ballX + 2 * ballRadius > wallLeft && ballX < wallLeft && ballY > wallTop && ballY < wallBottom) {
            ball.reverseHorizontalDirection(); // Reverse X velocity
            collisionBuffer = 1; // Reset buffer
            collisionDetected = true;
            System.out.println("Right collision detected");
        }

        // Check right side collision
        if (ballX < wallRight && ballX + 2 * ballRadius > wallRight && ballY > wallTop && ballY < wallBottom) {
            ball.reverseHorizontalDirection(); // Reverse X velocity
            collisionBuffer = 1; // Reset buffer
            collisionDetected = true;
            System.out.println("Left collision detected");
        }

        // Check vertical collision
        if (ballY + 2 * ballRadius > wallTop && ballY < wallTop && ballX > wallLeft && ballX < wallRight) {
            ball.reverseVerticalDirection(); // Reverse Y velocity
            collisionBuffer = 1; // Reset buffer
            collisionDetected = true;
            System.out.println("Bottom collision detected");
        }

        // Check bottom side collision
        if (ballY < wallBottom && ballY + 2 * ballRadius > wallBottom && ballX > wallLeft && ballX < wallRight) {
            ball.reverseVerticalDirection(); // Reverse Y velocity
            collisionBuffer = 1; // Reset buffer
            collisionDetected = true;
            System.out.println("Top collision detected");
        }
    } else {
        collisionBuffer++; // Increment buffer
    }

    // Handle color change for colored walls
    if (collisionDetected && Character.isDigit(wallType)) {
        String newColor = getWallColor(wallType);
        ball.setColor(newColor);
    }
}



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
