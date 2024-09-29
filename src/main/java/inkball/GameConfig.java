package inkball;

import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.core.PApplet;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GameConfig {
    private JSONObject config;
    private JSONArray levels;
    private HashMap<String, Integer> scoreIncreaseFromHoleCapture;
    private HashMap<String, Integer> scoreDecreaseFromWrongHole;

    public GameConfig(PApplet applet, String configPath) {
        // Load the JSON configuration file
        config = applet.loadJSONObject(configPath);
        levels = config.getJSONArray("levels");

        // Initialize score modifiers
        scoreIncreaseFromHoleCapture = new HashMap<>();
        scoreDecreaseFromWrongHole = new HashMap<>();
        
        JSONObject scoreIncrease = config.getJSONObject("score_increase_from_hole_capture");
        JSONObject scoreDecrease = config.getJSONObject("score_decrease_from_wrong_hole");

        // Populate the score modifiers
        for (Object key : scoreIncrease.keys()) {
            scoreIncreaseFromHoleCapture.put((String) key, scoreIncrease.getInt((String) key));
        }

        for (Object key : scoreDecrease.keys()) {
            scoreDecreaseFromWrongHole.put((String) key, scoreDecrease.getInt((String) key));
        }
    }

    // Method to get level attributes for a specific level index
    public JSONObject getLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IllegalArgumentException("Invalid level index.");
        }
        return levels.getJSONObject(index);
    }

    // Method to get balls for a specific level
    public List<String> getBalls(int levelIndex) {
        JSONObject level = getLevel(levelIndex);
        JSONArray ballsArray = level.getJSONArray("balls");
        List<String> balls = new ArrayList<>();
        for (int i = 0; i < ballsArray.size(); i++) {
            balls.add(ballsArray.getString(i));
        }
        return balls;
    }

    // Method to get score increase based on ball color
    public int getScoreIncrease(String color) {
        return scoreIncreaseFromHoleCapture.getOrDefault(color, 0);
    }

    // Method to get score decrease based on ball color
    public int getScoreDecrease(String color) {
        return scoreDecreaseFromWrongHole.getOrDefault(color, 0);
    }

    // Method to access other level-specific data
    public String getLayout(int levelIndex) {
        return getLevel(levelIndex).getString("layout");
    }

    public int getTime(int levelIndex) {
        return getLevel(levelIndex).getInt("time");
    }

    public int getSpawnInterval(int levelIndex) {
        return getLevel(levelIndex).getInt("spawn_interval");
    }

    public float getScoreIncreaseModifier(int levelIndex) {
        return getLevel(levelIndex).getFloat("score_increase_from_hole_capture_modifier");
    }

    public float getScoreDecreaseModifier(int levelIndex) {
        return getLevel(levelIndex).getFloat("score_decrease_from_wrong_hole_modifier");
    }
}
