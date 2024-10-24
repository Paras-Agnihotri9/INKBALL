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

    /** 
     *  Method to get level attributes for a specific level index
     * @param index used to know what level we are on
     * @return returns the attributes at that index
    */
    public JSONObject getLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IllegalArgumentException("Invalid level index.");
        }
        return levels.getJSONObject(index);
    }

    /** Method to get balls for a specific level
     * @param levelIndex is used to know what level we are at
     * @return balls it returns the balls that are to be spawned
    */
    public List<String> getBalls(int levelIndex) {
        JSONObject level = getLevel(levelIndex);
        JSONArray ballsArray = level.getJSONArray("balls");
        List<String> balls = new ArrayList<>();
        for (int i = 0; i < ballsArray.size(); i++) {
            balls.add(ballsArray.getString(i));
        }
        return balls;
    }

    /**
     * Method to get score increase based on ball color.
     * @param color color of the ball is taken to determine the score increase.
     * @return tells i]us the score increased from hole capture.
     */
    public int getScoreIncrease(String color) {
        return scoreIncreaseFromHoleCapture.getOrDefault(color, 0);
    }

    /**
     * Method to get score decrease based on ball color
     * @param color color of the ball is taken to determine the score decrease.
     * @return tells us the score decreased from hole capture.
     */
    public int getScoreDecrease(String color) {
        return scoreDecreaseFromWrongHole.getOrDefault(color, 0);
    }

    /**
     * Method to access other level-specific data
     * @param levelIndex is used to know what level we are at
     * @return returns what layout needs to be used
     */
    public String getLayout(int levelIndex) {
        return getLevel(levelIndex).getString("layout");
    }

    /**
     * Method to access level time
     * @param levelIndex is used to know what level we are at
     * @return the time the level should run for.
     */
    public int getTime(int levelIndex) {
        return getLevel(levelIndex).getInt("time");
    }

    /**
     * Method to access spawn interval
     * @param levelIndex is used to know what level we are at
     * @return the spawn interval for the game.
     */
    public int getSpawnInterval(int levelIndex) {
        return getLevel(levelIndex).getInt("spawn_interval");
    }

    /**
     * Method to access score increase modifier
     * @param levelIndex is used to know what level we are at
     * @return the score increase modifier for the level.
     */
    public float getScoreIncreaseModifier(int levelIndex) {
        return getLevel(levelIndex).getFloat("score_increase_from_hole_capture_modifier");
    }

    /**
     * Method to access score decrease modifier
     * @param levelIndex is used to know what level we are at
     * @return the score decrease modifier for the level.
     */
    public float getScoreDecreaseModifier(int levelIndex) {
        return getLevel(levelIndex).getFloat("score_decrease_from_wrong_hole_modifier");
    }
}
