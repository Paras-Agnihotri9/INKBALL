package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerLineTest {
    private PlayerLine playerLine;
    private static final int LINE_THICKNESS = 10;

    static App app;

    @BeforeEach
    public void setup() {
        app = new App(); // Initialize app before tests
        PApplet.runSketch(new String[]{"App"}, app); // Start Processing sketch for necessary tests
        playerLine = new PlayerLine(Color.BLACK, LINE_THICKNESS);
    }

    // Test that a new PlayerLine instance is initialized with the correct color and thickness
    @Test
    public void testInitialization() {
        assertEquals(Color.BLACK, playerLine.color);
        assertEquals(LINE_THICKNESS, playerLine.thickness);
        assertFalse(playerLine.isComplete()); // Should be incomplete by default
    }

    // Test adding a segment to the line and check if the segment count is correct
    @Test
    public void testAddSegment() {
        playerLine.addSegment(100, 100);
        assertEquals(1, playerLine.segments.size()); // Check that one segment is added
        assertEquals(new PVector(100, 100), playerLine.segments.get(0)); // Validate segment coordinates
    }

    // Test intersection detection with a point near the line segment
    @Test
    public void testIntersects() {
        playerLine.addSegment(50, 50);
        playerLine.addSegment(100, 100);
        assertTrue(playerLine.intersects(50, 50));
        assertTrue(playerLine.intersects(100, 100)); 
    }

    // Test collision detection between a ball and the line segment
    @Test
    public void testCheckCollisionWithBall() {
        Ball testBall = new Ball(45, 45, "blue", app.balls);
        playerLine.addSegment(50, 50);
        playerLine.addSegment(100, 100);
        
        // Test for a collision
        assertTrue(playerLine.checkCollisionWithBall(testBall)); // Should detect a collision
        assertTrue(testBall.hasCollided()); // Ball should be marked as collided

        // Test for no collision after setting the ball's position further away
        testBall.setX(200);
        testBall.setY(200);
        assertFalse(playerLine.checkCollisionWithBall(testBall)); // Should not detect a collision
    }

    // Test to ensure that the line is not complete when first initialized
    @Test
    public void testIsCompleteInitially() {
        assertFalse(playerLine.isComplete()); // Line should not be complete initially
    }

    // Test setting the line to complete
    @Test
    public void testSetComplete() {
        playerLine.setComplete(true);
        assertTrue(playerLine.isComplete()); // Line should be marked as complete
    }

    // Test the line rendering function (mocked)
    @Test
    public void testDisplay() {
        // Create a mock or test instance of PApplet
        playerLine.addSegment(50, 50);
        playerLine.addSegment(100, 100);
        assertDoesNotThrow(() -> playerLine.display(app)); // Should not throw an exception
    }

    // Test reflection of the ball upon collision
    @Test
    public void testReflectBall() {
        // Set up the test environment and the ball
        Ball testBall = new Ball(50, 50, "blue", app.balls);
        testBall.setVelocity(1, 1);
        playerLine.addSegment(0, 0);
        playerLine.addSegment(50, 50);
        
        // Simulate a collision
        assertTrue(playerLine.checkCollisionWithBall(testBall)); // Should collide
        assertNotEquals(1, testBall.velocityX); // Velocity should change upon reflection
        assertNotEquals(1, testBall.velocityY); // Velocity should change upon reflection
    }
}
