package inkball;

import java.awt.Color;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class PlayerLine {
    private ArrayList<PVector> segments; // Store the line segments
    private Color color;                 // Line color
    private int thickness;               // Line thickness
    private boolean complete;
    
    public PlayerLine(Color color, int thickness) {
        this.segments = new ArrayList<>();
        this.color = color;
        this.thickness = thickness;
        this.complete = false;
    }

    public boolean intersects(float mouseX, float mouseY) {
        for (PVector segment : segments) {
            float distance = PApplet.dist(mouseX, mouseY, segment.x, segment.y);
            if (distance <= thickness / 2) { // Check if within the line thickness
                return true;
            }
        }
        return false;
    }

    // Add a new segment to the line
    public void addSegment(float x, float y) {
        segments.add(new PVector(x, y));
    }

    // Render the line
    public void display(PApplet app) {
        app.stroke(color.getRGB());
        app.strokeWeight(thickness);
        for (int i = 0; i < segments.size() - 1; i++) {
            PVector start = segments.get(i);
            PVector end = segments.get(i + 1);
            app.line(start.x, start.y, end.x, end.y);
        }
    }

    // Check if the ball is colliding with the line segment defined by points P1 and P2
    // Check if the ball is colliding with the line segment defined by points P1 and P2
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


    private boolean isCollidingWithSegment(Ball ball, PVector p1, PVector p2) {
        // Ball collision detection logic (as previously written)
        PVector ballPosition = new PVector(ball.getX() + ball.getRadius(), ball.getY() + ball.getRadius());
        PVector ballVelocity = new PVector(ball.getVelocityX(), ball.getVelocityY());
        PVector predictedPosition = PVector.add(ballPosition, ballVelocity);
        float distanceToSegment = distToSegment(predictedPosition, p1, p2);
        return distanceToSegment <= ball.getRadius();
    }

    private float distToSegment(PVector point, PVector p1, PVector p2) {
        // Distance calculation logic (as previously written)
        float segmentLengthSquared = PApplet.dist(p1.x, p1.y, p2.x, p2.y) * PApplet.dist(p1.x, p1.y, p2.x, p2.y);
        if (segmentLengthSquared == 0) return PApplet.dist(point.x, point.y, p1.x, p1.y);
        float t = ((point.x - p1.x) * (p2.x - p1.x) + (point.y - p1.y) * (p2.y - p1.y)) / segmentLengthSquared;
        t = PApplet.constrain(t, 0, 1);
        PVector closestPoint = PVector.add(p1, PVector.sub(p2, p1).mult(t));
        return PApplet.dist(point.x, point.y, closestPoint.x, closestPoint.y);
    }

    private void reflectBall(Ball ball, PVector p1, PVector p2) {
        PVector ballVelocity = new PVector(ball.getVelocityX(), ball.getVelocityY());
        PVector normal = new PVector(-(p2.y - p1.y), p2.x - p1.x).normalize();
        
        float dotProduct = ballVelocity.dot(normal);
        PVector newVelocity = PVector.sub(ballVelocity, normal.mult(2 * dotProduct));
        
        // Optional: Normalize the new velocity if needed
        float maxVelocity = 5.0f; // Define a maximum velocity
        newVelocity.limit(maxVelocity); // Limit the velocity to prevent jitter

        ball.setVelocity(newVelocity.x, newVelocity.y);
    }

    public void addSegment(int x, int y) {
        segments.add(new PVector(x, y));
    }


    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

}
