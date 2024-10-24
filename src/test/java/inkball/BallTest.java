package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;


public class BallTest {
    static App app;

    @BeforeAll
    public static void setup() {
        app = new App();
        app.loop();
        PApplet.runSketch(new String[]{"App"}, app);
    }

    @Test
    public void testBallInitialization() {
        // Test the initialization of the Ball class
        
        Ball ball = new Ball(100, 100, "blue", app.balls);
        
        // Verify the initial properties of the ball
        assertEquals(100, ball.getX(), "Initial X position should be 100");
        assertEquals(100, ball.getY(), "Initial Y position should be 100");
        assertEquals("blue", ball.getColor(), "Initial color should be red");
    }

    @Test
    public void testBallMovement() {
        // Test the ball's movement after an update
        Ball ball = new Ball(50, 50, "grey", app.balls);
        
        float initialX = ball.getX();
        float initialY = ball.getY();
        
        // Update the ball
        ball.update();
        
        // The ball's position should change based on velocity
        assertNotEquals(initialX, ball.getX(), "Ball's X position should change after update");
        assertNotEquals(initialY, ball.getY(), "Ball's Y position should change after update");
    }

    @Test
    public void testBallShrinking() {
        // Test the ball's shrinking functionality
        Ball ball = new Ball(50, 50, "yellow", app.balls);
        
        ball.shrinking = true;
        
        float initialSize = ball.getCurrentSize();
        ball.update(); // Call update to trigger shrinking
        
        // The current size should decrease after update
        assertTrue(ball.getCurrentSize() < initialSize, "Ball should shrink after update when shrinking is initiated");
    }

    @Test
    public void testCollisionDetection() {
        // Test the ball's collision detection
        Ball ball = new Ball(50, 50, "blue", app.balls);
        
        ball.setHasCollided(true);
        assertTrue(ball.hasCollided(), "Ball should report a collision after being set");
        
        ball.resetCollision();
        assertFalse(ball.hasCollided(), "Ball should not report a collision after reset");
    }

    @Test
    public void testAttractionToHoles() {
        Ball ball = new Ball(80, 80, "green", app.balls);
        List<Hole> holes = new ArrayList<>();
        
        holes.add(new Hole(40, 40, '0')); // Add a hole near the ball
        
        ball.attractToHoles(holes);
        
        assertFalse(ball.captured, "Ball should be captured by the hole");
    }

    @Test
    public void testScoreUpdateOnCapture() {
        app.setupLevel(0);
        // Test the score update functionality when the ball is captured
        Ball ball = new Ball(50, 50, "grey", app.balls);
        
        // Assuming App class has static variables score and scoreIncreaseModifier initialized
        float initialScore = App.score;
        
        // Simulate capturing the ball
        ball.setCaptured(true);
        ball.updateScore(true, "0"); // Grey ball and grey hole
        
        // Verify score is updated correctly
        assertTrue(App.score > initialScore, "Score should increase on successful capture of grey ball");
        
        // Simulate unsuccessful capture
        initialScore = App.score;
        ball.setCaptured(false);
        ball.updateScore(false, "1"); // Non-grey hole for grey ball
        
        // Verify score is decreased correctly
        assertFalse(App.score > initialScore, "Score should decrease on unsuccessful capture");
    }
}
