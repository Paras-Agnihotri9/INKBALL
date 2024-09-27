package inkball;

import java.util.Collections;
import java.util.List;

public class Level {
    private String layout;
    private int time;
    private int spawnInterval;
    private List<String> balls;

    public Level(String layout, int time, int spawnInterval, List<String> balls) {
        if (time < 0) throw new IllegalArgumentException("Time must be non-negative");
        if (spawnInterval <= 0) throw new IllegalArgumentException("Spawn interval must be positive");
        this.layout = layout;
        this.time = time;
        this.spawnInterval = spawnInterval;
        this.balls = balls;
    }

    public String getLayout() {
        return layout;
    }

    public int getTime() {
        return time;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public List<String> getBalls() {
        return Collections.unmodifiableList(balls);
    }

    @Override
    public String toString() {
        return "Level{" +
                "layout='" + layout + '\'' +
                ", time=" + time +
                ", spawnInterval=" + spawnInterval +
                ", balls=" + balls +
                '}';
    }
}
