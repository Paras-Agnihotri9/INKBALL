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

    // Add a new segment to the line
    public void addSegment(float x, float y) {
        segments.add(new PVector(x, y));
    }

    // Remove the line if right-clicked and intersected
    public boolean intersects(float mouseX, float mouseY) {
        for (PVector segment : segments) {
            float distance = PApplet.dist(mouseX, mouseY, segment.x, segment.y);
            if (distance <= thickness / 2) { // Check if within the line thickness
                return true;
            }
        }
        return false;
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

    // Getters for line data if needed
    public ArrayList<PVector> getSegments() {
        return segments;
    }
}
