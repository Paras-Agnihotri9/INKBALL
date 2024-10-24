package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WallTest {

private static final float EPSILON = 0.1f;

    Ball testBall;

    @BeforeEach
    public void setupBall() {
        testBall = new Ball(50, 50, "blue", null); 
    }

    @Test
    public void testGetX() {
        Wall wall = new Wall(10, 12, '0');
        assertEquals(10, wall.getX());
    }

    @Test
    public void testGetY() {
        Wall wall = new Wall(10, 20, '1');
        assertEquals(20, wall.getY());
    }

    @Test
    public void testGetWallType() {
        Wall wall = new Wall(14, 12, '3');
        assertEquals('3', wall.getWallType());
    }

    @Test
    public void testGetWallColor() {
        Wall wall = new Wall(0, 12, 'X');
        assertEquals("grey", wall.getWallColor('X'));
    }

    @Test
    public void testIsOverlapping() {
        Wall wall = new Wall(5, 5, 'X');
        testBall.setX(45);
        testBall.setY(45);

        assertFalse(wall.isOverlapping(testBall)); // Ball overlaps with the wall
    }

    @Test
    public void testIsNotOverlapping() {
        Wall wall = new Wall(10, 10, 'X');
        testBall.setX(200); // Move ball far away from the wall
        testBall.setY(200);

        assertFalse(wall.isOverlapping(testBall)); // No overlap
    }

    @Test
    public void testHandleCollisionLeftSide() {
        Wall wall = new Wall(5, 5, 'X');
        testBall.setX(30); // Set ball near the left side of the wall
        testBall.setY(40);

        wall.handleCollision(testBall);

        assertTrue(testBall.velocityX < -EPSILON); // Ball velocity should be reversed horizontally
    }

    @Test
    public void testHandleCollisionRightSide() {
        Wall wall = new Wall(5, 5, 'X');
        testBall.setX(70); // Set ball near the right side of the wall
        testBall.setY(40);
        wall.handleCollision(testBall);
        assertTrue(testBall.velocityX > EPSILON); // Ball velocity should be reversed horizontally
    }

    @Test
    public void testHandleCollisionTopSide() {
        Wall wall = new Wall(5, 5, 'X');
        testBall.setX(40);
        testBall.setY(30); // Set ball near the top side of the wall

        wall.handleCollision(testBall);
        assertTrue(testBall.velocityY > EPSILON); // Ball velocity should be reversed vertically
    }

    @Test
    public void testHandleCollisionBottomSide() {
        Wall wall = new Wall(5, 5, 'X');
        testBall.setX(40);
        testBall.setY(70); // Set ball near the bottom side of the wall

        wall.handleCollision(testBall);
        assertTrue(testBall.velocityY < -EPSILON); // Ball velocity should be reversed vertically
    }

    @Test
    public void testHandleColoredWallCollision() {
        Wall wall = new Wall(5, 5, '1'); // Orange wall
        testBall.setX(45);
        testBall.setY(45);
        wall.handleCollision(testBall);
        assertEquals("blue", testBall.getColor());
    }
}
