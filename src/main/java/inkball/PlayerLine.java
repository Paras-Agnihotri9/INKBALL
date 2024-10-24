package inkball;

import java.awt.Color;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class PlayerLine {
    public ArrayList<PVector> segments; // Store the line segments
    public Color color;                 // Line color
    public int thickness;               // Line thickness
    private boolean complete;
    
    public PlayerLine(Color color, int thickness) {
        this.segments = new ArrayList<>();
        this.color = color;
        this.thickness = thickness;
        this.complete = false;
    }

    /**
     * Checks whether there is an interlap between the mouse and the segments of the line
     * @param mouseX the x coordinate of the mouse position on the board
     * @param mouseY the y coordinate of the mouse position on the board
     * @return true if it intersects false if it doesnnt.
     */
    public boolean intersects(float mouseX, float mouseY) {
        for (PVector segment : segments) {
            float distance = PApplet.dist(mouseX, mouseY, segment.x, segment.y);
            if (distance <= thickness / 2) { // Check if within the line thickness
                return true;
            }
        }
        return false;
    }

    /**
     * Add a new segment to the line
     * @param x  coordinate of the linesegment
     * @param y  coordinate of the linesegment
     */
    public void addSegment(float x, float y) {
        segments.add(new PVector(x, y));
    }

    /**
     * displays the lines on the board
     * @param app takes in app to draw the player lines
     */
    public void display(PApplet app) {
        app.stroke(color.getRGB());
        app.strokeWeight(thickness);
        for (int i = 0; i < segments.size() - 1; i++) {
            PVector start = segments.get(i);
            PVector end = segments.get(i + 1);
            app.line(start.x, start.y, end.x, end.y);
        }
    }

    /**
     * Checks if the ball is colliding with the line segment defined by points P1 and P2
     * @param ball used to check for collision with a ball
     * @return true if collided false if not collided
     */
    public boolean checkCollisionWithBall(Ball ball) {
        if (ball.hasCollided()) return false; // Ignore if already collided

        for (int i = segments.size() - 1; i >= 0; i--) {
            PVector p1 = segments.get(i);
            PVector p2 = (i == segments.size() - 1) ? segments.get(i) : segments.get(i + 1);
            
            if (isCollidingWithSegment(ball, p1, p2)) {
                System.out.println("Collision detected with segment: " + i);
                reflectBall(ball, p1, p2);
                ball.setHasCollided(true); // Set collision flag
                return true; // Return true if collision occurs
            }
        }
        return false; // No collision
    }


    /**
     * Checks if the ball is colliding with 2 P vectors
     * @param ball to determine the collision we take in the ball
     * @param p1 one point of the segemnt
     * @param p2 another point of the segment
     * @return true if collided else false
     */
    public boolean isCollidingWithSegment(Ball ball, PVector p1, PVector p2) {
        PVector ballPosition = new PVector(ball.getX() + ball.getRadius(), ball.getY() + ball.getRadius());
        PVector ballVelocity = new PVector(ball.velocityX, ball.velocityY);
        PVector predictedPosition = PVector.add(ballPosition, ballVelocity);
        float distanceToSegment = distToSegment(predictedPosition, p1, p2);
        return distanceToSegment <= ball.getRadius();
    }

    /**
     * Calculates distance to the line segment
     * @param point a point to check the distance from
     * @param p1 one point of the segemnt
     * @param p2 another point of the segment
     * @return the distance in between the points
     */
    public float distToSegment(PVector point, PVector p1, PVector p2) {
        float segmentLengthSquared = PApplet.dist(p1.x, p1.y, p2.x, p2.y) * PApplet.dist(p1.x, p1.y, p2.x, p2.y);
        if (segmentLengthSquared == 0) return PApplet.dist(point.x, point.y, p1.x, p1.y);
        float t = ((point.x - p1.x) * (p2.x - p1.x) + (point.y - p1.y) * (p2.y - p1.y)) / segmentLengthSquared;
        t = PApplet.constrain(t, 0, 1);
        PVector closestPoint = PVector.add(p1, PVector.sub(p2, p1).mult(t));
        return PApplet.dist(point.x, point.y, closestPoint.x, closestPoint.y);
    }

    /**
     * reflects the ball according to the hitbox logic
     * @param ball so that the ball can be reflected
     * @param p1 uses a vector to normalise for hitbox logic
     * @param p2 uses a vector to normalise for hitbox logic
     */
    public void reflectBall(Ball ball, PVector p1, PVector p2) {
        PVector ballVelocity = new PVector(ball.velocityX, ball.velocityY);
        PVector normal = new PVector(-(p2.y - p1.y), p2.x - p1.x).normalize();
        
        float dotProduct = ballVelocity.dot(normal);
        PVector newVelocity = PVector.sub(ballVelocity, normal.mult(2 * dotProduct));
        
        // Optional: Normalize the new velocity if needed
        float maxVelocity = 5.0f; // Define a maximum velocity
        newVelocity.limit(maxVelocity); // Limit the velocity to prevent jitter

        ball.setVelocity(newVelocity.x, newVelocity.y);
    }

    /**
     * Adds the a line segment
     * @param x coordinate of the segments
     * @param y coordinate of the segments
     */
    public void addSegment(int x, int y) {
        segments.add(new PVector(x, y));
    }

    /**
     * returns if the line is complete or not
     * @return true if the line is complete else false
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * it sets the line to complete
     * @param complete takes in complete to set it
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

}
