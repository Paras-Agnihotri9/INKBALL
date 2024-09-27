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

        // Initialize the score modifiers
        scoreIncreaseFromHoleCapture = new HashMap<>();
        scoreDecreaseFromWrongHole = new HashMap<>();
        
        JSONObject scoreIncrease = config.getJSONObject("score_increase_from_hole_capture");
        JSONObject scoreDecrease = config.getJSONObject("score_decrease_from_wrong_hole");

        // Populate the score modifiers
        for (Object key : scoreIncrease.keys()) { // Change String to Object
            scoreIncreaseFromHoleCapture.put((String) key, scoreIncrease.getInt((String) key)); // Cast to String
        }

        for (Object key : scoreDecrease.keys()) { // Change String to Object
            scoreDecreaseFromWrongHole.put((String) key, scoreDecrease.getInt((String) key)); // Cast to String
        }
    }

    public List<Level> getLevels() {
        List<Level> levelList = new ArrayList<>();
        for (int i = 0; i < levels.size(); i++) {
            JSONObject levelObj = levels.getJSONObject(i);
            JSONArray ballsArray = levelObj.getJSONArray("balls");
            List<String> balls = new ArrayList<>();
            for (int j = 0; j < ballsArray.size(); j++) {
                balls.add(ballsArray.getString(j)); // Convert JSONArray to List<String>
            }
            Level level = new Level(
                levelObj.getString("layout"),
                levelObj.getInt("time"),
                levelObj.getInt("spawn_interval"),
                balls // Pass the List<String> of balls
            );
            levelList.add(level);
        }
        return levelList;
    }

    public int getScoreIncrease(String color) {
        return scoreIncreaseFromHoleCapture.getOrDefault(color, 0);
    }

    public int getScoreDecrease(String color) {
        return scoreDecreaseFromWrongHole.getOrDefault(color, 0);
    }
}
