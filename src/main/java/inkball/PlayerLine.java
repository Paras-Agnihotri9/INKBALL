package inkball;

import java.awt.Color;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class PlayerLine {
    private ArrayList<PVector> segments; // Store the line segments
    private Color color;                 // Line color
    private int thickness;               // Line thickness

    public PlayerLine(Color color, int thickness) {
        this.segments = new ArrayList<>();
        this.color = color;
        this.thickness = thickness;
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
    public boolean checkCollisionWithBall(Ball ball) {
        if (ball.hasCollided()) return false; // Ignore if already collided

        for (int i = segments.size() - 1; i >= 0; i--) { // Iterate backward for safe removal
            PVector p1 = segments.get(i);
            PVector p2 = (i == segments.size() - 1) ? segments.get(i) : segments.get(i + 1);

            if (isCollidingWithSegment(ball, p1, p2)) {
                reflectBall(ball, p1, p2);
                segments.remove(i); // Remove the specific segment that was hit
                ball.setHasCollided(true); // Set collision flag
                return true; // Collision occurred
            }
        }
        return false; // No collision
    }

    // Check if the ball is colliding with the line segment
    private boolean isCollidingWithSegment(Ball ball, PVector p1, PVector p2) {
    PVector ballPosition = new PVector(ball.getX() + ball.getRadius(), ball.getY() + ball.getRadius());
    PVector ballVelocity = new PVector(ball.getVelocityX(), ball.getVelocityY());

    // Predict the ball's future position
    PVector predictedPosition = PVector.add(ballPosition, ballVelocity);

    // Calculate the distance from the ball's predicted position to the line segment
    float distanceToSegment = distToSegment(predictedPosition, p1, p2);

    // Check for collision considering the ball's radius
    return distanceToSegment <= ball.getRadius();
}

// Method to calculate the distance from a point to a line segment
private float distToSegment(PVector point, PVector p1, PVector p2) {
    float segmentLengthSquared = PApplet.dist(p1.x, p1.y, p2.x, p2.y) * PApplet.dist(p1.x, p1.y, p2.x, p2.y);
    
    if (segmentLengthSquared == 0) return PApplet.dist(point.x, point.y, p1.x, p1.y); // p1 and p2 are the same point

    // Calculate the projection of point onto the line segment
    float t = ((point.x - p1.x) * (p2.x - p1.x) + (point.y - p1.y) * (p2.y - p1.y)) / segmentLengthSquared;

    // Clamp t to the line segment
    t = PApplet.constrain(t, 0, 1);

    // Find the closest point on the line segment
    PVector closestPoint = PVector.add(p1, PVector.sub(p2, p1).mult(t));

    // Return the distance from the point to the closest point on the segment
    return PApplet.dist(point.x, point.y, closestPoint.x, closestPoint.y);
}


    // Reflect the ball's velocity based on the line segment it collides with
    public void reflectBall(Ball ball, PVector p1, PVector p2) {
        PVector ballVelocity = new PVector(ball.getVelocityX(), ball.getVelocityY());

        // Calculate the normal vector of the line segment
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        PVector normal = new PVector(-dy, dx).normalize(); // Normal vector

        // Calculate the dot product
        float dotProduct = ballVelocity.dot(normal);

        // Reflect the ball's velocity
        PVector newVelocity = PVector.sub(ballVelocity, normal.mult(2 * dotProduct));
        ball.setVelocity(newVelocity.x, newVelocity.y);
    }
}
