package inkball;

import processing.core.PApplet;

public class Hole {
    private float x; // X position of the hole
    private float y; // Y position of the hole
    public char type;
    private static final float HOLE_RADIUS = 32; // Radius for attraction zone
    private static final float ATTRACTION_FORCE = 0.005f; // 0.5% as a proportion

    public Hole(float x, float y, char type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCenterX() {
        return ((x+1) * App.CELLHEIGHT); // Center X of the hole
    }

    public float getCenterY() {
        return ((y+1) * App.CELLHEIGHT); // Center Y of the hole
    }

}


