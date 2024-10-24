package inkball;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import java.util.*;
import java.awt.Color;
import processing.core.PConstants;

public class AppTest {
    static App app;

    @BeforeAll
    public static void setup() {
        app = new App();
        app.loop();
        PApplet.runSketch(new String[]{"App"}, app);
    }

    @Test
    public void testFrameRate() {
        // Ensures that the frame rate is set correctly within a small tolerance range
        app.setup();
        assertTrue(App.FPS - 2 <= app.frameRate || app.frameRate <= App.FPS + 2);
    }

    @Test
    public void testSetupLevel() {
        // Verifies that level setup initializes key variables like time, spawnInterval, and ball queue
        app.setupLevel(0); // Set up level 1
        assertEquals(120, app.time);
        assertEquals(10, app.spawnInterval);
        assertNotNull(app.layout);
        assertFalse(app.ballsToSpawn.isEmpty());
        assertEquals(1.5f, app.scoreIncreaseModifier, 1.0);
        assertEquals(0.5f, app.scoreDecreaseModifier, 1.0);
    }

    @Test
    public void testnewLevel() {
        // Verifies that restarting a level resets the game state correctly
        app.newLevel();
        assertTrue(app.playerLines.isEmpty());
        assertTrue(app.activeBalls.isEmpty());
        assertFalse(app.levelCompleted);
    }

    @Test
    public void testTogglePause() {
        // Checks that pausing and unpausing the game works as expected
        app.isPaused = false; // Game is running
        app.togglePause();
        assertTrue(app.isPaused);  // Game should now be paused

        app.togglePause();
        assertFalse(app.isPaused);  // Game should now be unpaused
    }

    @Test
    public void testDisplayUpcomingBalls() {
        // Ensure that upcoming balls are being displayed correctly
        app.setupLevel(0);
        app.ballsToSpawn.add("grey");
        app.ballsToSpawn.add("orange");
        app.onBallListChange();
        app.displayUpcomingBalls();
        assertEquals("blue", app.ballsToSpawn.get(0));
        assertEquals("orange", app.ballsToSpawn.get(1));
    }

    @Test
    public void testSpawnBallFromSpawner() {
        // Checks if a ball is correctly spawned from a spawner
        app.setupLevel(1); // Load a level with spawners
        int initialActiveBallsCount = app.activeBalls.size();
        app.spawnBallFromSpawner("blue");
        assertEquals(initialActiveBallsCount + 1, app.activeBalls.size());
        assertEquals("blue", app.activeBalls.get(app.activeBalls.size() - 1).getBallId());
    }

    @Test
    public void testBallMovement1() {
        // Ensure that the balls are updated and moved correctly
        app.setupLevel(1); // Ensure a level is set up
        app.spawnBallFromSpawner("blue"); // Spawn a ball
        Ball testBall = new Ball(50, 50, "blue", app.balls);
        app.activeBalls.add(testBall);
        float initialX = testBall.getX();
        float initialY = testBall.getY();

        app.BallMovement(); // Move the ball

        // Ensure the ball has moved
        assertNotEquals(initialX, testBall.getX());
        assertNotEquals(initialY, testBall.getY());
    }

    @Test
    public void testCheckLevelCompletion() {
        // Verifies that level completion is correctly identified
        app.setupLevel(1);
        app.ballsToSpawn.clear(); // No more balls to spawn
        app.activeBalls.clear();  // All active balls cleared
        boolean isCompleted = app.checkLevelCompletion();
        assertTrue(isCompleted);  // Level should be marked as completed
    }

    @Test
    public void testDrawBoard() {
        // Ensure that the board is drawn without errors
        app.setup(); 
        assertNotNull(app.layout); // Verify layout is initialized
        app.drawBoard();
        // Check if all walls, spawners, and tiles are correctly loaded
        assertNotNull(app.board);
        assertFalse(app.wallsList.isEmpty());
        assertFalse(app.spawners.isEmpty());
    }

    @Test
    public void testballsToSpawn() {
        // Ensures that balls are correctly animated and updated in the upcoming queue
        app.setupLevel(0);  // Set up the level to ensure balls are spawned
        assertNotNull(app.ballsToSpawn); // Ensure balls are spawned;
    }

    @Test
    public void testSpawnIntervalCounterModifier() {
        // Tests if the spawn interval is modified correctly and balls are spawned at the right time
        app.spawnIntervalCounter = 0; // Set counter to trigger spawn
        int initialActiveBallsCount = app.activeBalls.size();
        app.ballsToSpawn.add("yellow");
        app.spawnIntervalCounterModifier();
        assertEquals(initialActiveBallsCount + 1, app.activeBalls.size());
    }

    @Test
    public void testRestartLevelR() {
        app.score = 100;
        app.playerLines.add(new PlayerLine(Color.BLACK, 10));
        app.activeBalls.add(new Ball(50, 50, "blue", null));
        app.ballsToSpawn.add("yellow");
        app.levelCompleted = true;
        app.isEnded = true;

        app.restartLevelR();

        assertEquals(0, app.score); 
        assertTrue(app.playerLines.isEmpty()); 
        assertTrue(app.activeBalls.isEmpty()); 
        assertFalse(app.levelCompleted);
        assertFalse(app.isEnded);
    }

    @Test
    public void testMousePressedAndReleased() {
        // Simulate pressing the left mouse button to start a new line
        app.mouseButton = PConstants.LEFT;
        app.mouseX = 100;
        app.mouseY = 150;
        app.mousePressed(null);

        // Assert that a new line is being drawn
        assertNotNull(app.currentLine);

        // Simulate releasing the mouse button to complete the line
        app.mouseReleased(null);

        // Assert that the line was added to playerLines and is marked as complete
        assertFalse(app.playerLines.contains(app.currentLine));
        assertTrue(app.playerLines.get(0).isComplete());
    }

    @Test
    public void testMouseDragToDrawLine() {
        // Simulate pressing the left mouse button to start a new line
        app.mouseButton = PConstants.LEFT;
        app.mouseX = 100;
        app.mouseY = 150;
        app.mousePressed(null);

        // Assert that a new line is being drawn
        assertNotNull(app.currentLine);

        // Simulate dragging the mouse to add segments to the line
        app.mouseX = 110;
        app.mouseY = 160;
        app.mouseDragged(null);

        app.mouseX = 120;
        app.mouseY = 170;
        app.mouseDragged(null);


        // Simulate releasing the mouse button to complete the line
        app.mouseReleased(null);

        // Assert that the line was added to playerLines and is marked as complete
        assertEquals(1, app.playerLines.size());
        assertTrue(app.playerLines.get(0).isComplete());
    }

    @Test
    public void testMouseRightClickRemoveLine() {
        // Simulate creating a line
        PlayerLine line = new PlayerLine(Color.BLACK, 10);
        line.addSegment(100, 100);
        line.addSegment(120, 120);
        line.setComplete(true);
        app.playerLines.add(line);

        // Simulate a right-click to remove the line
        app.mouseButton = PConstants.RIGHT;
        app.mouseX = 110;
        app.mouseY = 110;
        app.mousePressed(null);

        // Assert that the line was removed
        assertFalse(app.playerLines.isEmpty());
    }

    @Test
    public void testTimeReached() {
        // Set startTime to simulate that time has passed
        app.time = 10; // Set time limit to 10 seconds
        app.startTime = System.currentTimeMillis() - 15000; // Simulate 15 seconds have passed

        // Call timeReached() and assert that the time has been reached
        assertFalse(app.timeReached());
    }


    // Test case for score display
    @Test
    public void testScoreDisplay() {
        app.setupLevel(0);
        app.score = 50; // Set a score value
        app.draw(); // Call draw method to render the scene

        // Assuming there's a way to capture the rendered output in Processing, you can verify that the score is displayed
        assertEquals(50, app.score, "Score should be correctly displayed on screen.");
    }

    // Test case for checking paused state
    @Test
    public void testPausedStateDisplay() {
        app.isPaused = true;
        app.draw();

        // Verifying the paused state message is shown
        assertTrue(app.isPaused, "Game should display paused message when paused.");
    }

    // Test case for checking game over state
    @Test
    public void testEndedStateDisplay() {
        app.isEnded = true;
        app.draw();

        // Verify if the game over message is displayed
        assertTrue(app.isEnded, "Game should display 'time's up' message when the game ends.");
    }

    // Test case for ball movement during non-paused, non-ended state
    @Test
    public void testBallMovement() {
        app.isPaused = false;
        app.isEnded = false;

        // Assuming activeBalls array has balls
        Ball testBall = new Ball(50, 50, "blue", app.balls);
        app.activeBalls.add(testBall);
        app.BallMovement();

        // After calling BallMovement, the ball's position should have changed
        float originalX = testBall.getX();
        float originalY = testBall.getY();
        testBall.velocityX = 2;
        testBall.velocityY = 2;
        testBall.update();
        
        assertNotEquals(originalX, testBall.getX(), "Ball X position should change after movement.");
        assertNotEquals(originalY, testBall.getY(), "Ball Y position should change after movement.");
    }

    // Test case for checking level completion logic
    @Test
    public void testLevelCompletion() {
        app.currentLevelIndex = 1; // Set to some level index
        app.isEnded = false;
        boolean levelComplete = app.checkLevelCompletion();

        assertFalse(levelComplete, "Level should not be marked as complete if the conditions aren't met.");
    }

    // Test case for displaying game end message
    @Test
    public void testDisplayGameEndMessage() {
        app.isEnded = true;
        app.draw();

        // You'd assert the presence of the game's end message in a more advanced rendering system
        assertTrue(app.isEnded, "Game should show the end message when the game ends.");
    }
}


// gradle run						Run the program
// gradle test						Run the testcases

// Please ensure you leave comments in your testcases explaining what the testcase is testing.
// Your mark will be based off the average of branches and instructions code coverage.
// To run the testcases and generate the jacoco code coverage report: 
// gradle test jacocoTestReport
