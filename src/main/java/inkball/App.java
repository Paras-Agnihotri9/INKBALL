package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();

    private HashMap<String, String> ballColorMap;
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }
    
    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */

   
    PImage tile;
    PImage spawner;
    PImage[] walls;
    PImage[] balls;
    PImage[] holes;
    boolean[][] occupied;
    int startTime;


    private void initializeBallColorMap() {
        ballColorMap = new HashMap<>();
        ballColorMap.put("B0", "grey");
        ballColorMap.put("B1", "orange");
        ballColorMap.put("B2", "blue");
        ballColorMap.put("B3", "green");
        ballColorMap.put("B4", "yellow");
        // Add more mappings if necessary
    }
	@Override
    public void setup() {
        frameRate(FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		// the image is loaded from relative path: "src/main/resources/inkball/..."
		/*try {
            result = loadImage(URLDecoder.decode(this.getClass().getResource(filename+".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }*/
    
        startTime = millis();
        occupied =  new boolean[BOARD_WIDTH][BOARD_HEIGHT];
        int levelIndex = 0;
        GameConfig gameConfig = new GameConfig(this, "config.json");
        int time = gameConfig.getTime(levelIndex);
        int spawnInterval = gameConfig.getSpawnInterval(levelIndex);
        String layout = gameConfig.getLayout(levelIndex);
        List<String> balls_ingame = gameConfig.getBalls(levelIndex);
        float scoreIncreaseModifier = gameConfig.getScoreIncreaseModifier(levelIndex);
        float scoreDecreaseModifier = gameConfig.getScoreDecreaseModifier(levelIndex);

        // Access score modifiers
        int scoreIncreaseForBlue = gameConfig.getScoreIncrease("blue");
        int scoreDecreaseForYellow = gameConfig.getScoreDecrease("yellow");

        // Load images for walls
        walls = new PImage[5];
        for (int i = 0; i < 5; i++) {
            walls[i] = loadImage("src/main/resources/inkball/wall" + i + ".png");
        }

        // Load images for balls
        balls = new PImage[5];
        for (int i = 0; i < 5; i++) {
            balls[i] = loadImage("src/main/resources/inkball/ball" + i + ".png");
        }

        // Load images for holes
        holes = new PImage[5];
        for (int i = 0; i < 5; i++) {
            holes[i] = loadImage("src/main/resources/inkball/hole" + i + ".png");
        }

        // Load other images
        tile = loadImage("src/main/resources/inkball/tile.png");
        spawner = loadImage("src/main/resources/inkball/entrypoint.png");
        println(layout);
        loadLevel(layout);
        initializeBallColorMap();
        if (board == null) {
            println("Failed to load the board");
        } else {
            println("Board loaded successfully");
        }
        }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
    }

    @Override
    public void mouseReleased(MouseEvent e) {
		
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        
       //String filepath = 

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        //TODO
        drawBoard();
        fill(0);
        textSize(15);
        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        text("Score: ", WIDTH - 150, 30); 
        
        //----------------------------------
        //display time
        //----------------------------------
        //TODO
        int elapsedTime = (millis() - startTime) / 1000;
        text("Time: " + elapsedTime, WIDTH - 150, 60);
		//----------------------------------
        //----------------------------------
		//display game end message

    }

    char[][] board;

    public void loadLevel(String filepath) {
        board = new char[BOARD_WIDTH][BOARD_HEIGHT];
        String[] lines = loadStrings(filepath);

        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                board[x][y+2] = lines[y].charAt(x);
            }
        }
    }

    private void displayTime() {
            long elapsedTime = (millis() - startTime) / 1000; // Calculate elapsed time in seconds

            String timeString = String.valueOf(elapsedTime); // Convert to String

            // Display the time on the right side of the top bar
            textSize(20);
            fill(0);
            text("Time: " + timeString, WIDTH - 150, TOPBAR / 2 + 10); // Adjust the position as needed
        }

    private void drawBoard() {
    for (int y = 2; y < BOARD_HEIGHT; y++) {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            if (occupied[x][y]) {
                continue;
            }
            
            char tileType = board[x][y];
            
            switch (tileType) {
                case 'X':
                    image(walls[0], x * CELLSIZE, y * CELLHEIGHT);
                    break;
                case '1':
                    image(walls[1], x * CELLSIZE, y * CELLHEIGHT);
                    break;
                case '2':
                    image(walls[2], x * CELLSIZE, y * CELLHEIGHT);
                    break;
                case '3':
                    image(walls[3], x * CELLSIZE, y * CELLHEIGHT);
                    break;
                case '4':
                    image(walls[4], x * CELLSIZE, y * CELLHEIGHT);
                    break;
                case 'S':
                    image(spawner, x * CELLSIZE, y * CELLHEIGHT);
                    break;
                case 'B':
                    // Check for balls and their color
                    if (x + 1 < BOARD_WIDTH && Character.isDigit(board[x + 1][y])) {
                        String ballId = String.valueOf(tileType) + board[x + 1][y]; // e.g., "B0", "B1"
                        int ballType = Character.getNumericValue(board[x + 1][y]);
                        String ballColor = ballColorMap.get(ballId);
                        if (ballColor != null) {
                            // Call your ball spawning function here
                            // spawnBall(ballColor, x * CELLSIZE, y * CELLHEIGHT);
                        }
                        // Mark the cell as occupied
                        occupied[x][y] = true;
                        x++; // Skip the next x since the ball takes up two columns
                    }
                    break;
                default:
                    // Check if it's a hole (e.g., H0, H1, etc.)
                    if (tileType == 'H' && x + 1 < BOARD_WIDTH && Character.isDigit(board[x + 1][y])) {
                        int holeType = Character.getNumericValue(board[x + 1][y]);
                        image(holes[holeType], x * CELLSIZE, y * CELLHEIGHT, CELLSIZE * 2, CELLHEIGHT * 2); // Double size

                        // Mark the cells that are occupied by this larger hole
                        occupied[x][y] = true;
                        if (x + 1 < BOARD_WIDTH) {
                            occupied[x + 1][y] = true;  // Right cell
                        }
                        if (y + 1 < BOARD_HEIGHT) {
                            occupied[x][y + 1] = true;  // Bottom cell
                        }
                        if (x + 1 < BOARD_WIDTH && y + 1 < BOARD_HEIGHT) {
                            occupied[x + 1][y + 1] = true;  // Bottom-right cell
                        }

                        // Since the hole occupies two cells, skip the next x
                        x++; 
                    } else {
                        image(tile, x * CELLSIZE, y * CELLHEIGHT); // Empty tile
                    }
                    break;
            }
        }
    }
}

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
