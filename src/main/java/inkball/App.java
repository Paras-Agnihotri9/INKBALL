package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.core.PConstants;


import java.awt.Color;
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

    public boolean flag = true;

    private PlayerLine currentLine; // Current line being drawn

    private ArrayList<PlayerLine> playerLines; // All player-drawn lines


    List<int[]> spawners;

    int time;
    int spawnInterval;
    String layout;
    List<String> ballsToSpawn;
    float scoreIncreaseModifier;
    float scoreDecreaseModifier;
    int spawnIntervalCounter;
    List<Ball> activeBalls; // List to hold active balls
    List<Wall> wallsList;
    int startTime;


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
    public PImage[] balls;
    public PImage[] holes;
    boolean[][] occupied;
    boolean [][] occupiedBall;


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
    
        occupied =  new boolean[BOARD_WIDTH][BOARD_HEIGHT];
        occupiedBall = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
        playerLines = new ArrayList<>();
        int levelIndex = 0;
        GameConfig gameConfig = new GameConfig(this, "config.json");
        this.time = gameConfig.getTime(levelIndex);
        this.spawnInterval = gameConfig.getSpawnInterval(levelIndex);
        this.layout = gameConfig.getLayout(levelIndex);
        this.ballsToSpawn = gameConfig.getBalls(levelIndex);
        this.scoreIncreaseModifier = gameConfig.getScoreIncreaseModifier(levelIndex);
        this.scoreDecreaseModifier = gameConfig.getScoreDecreaseModifier(levelIndex);
        this.spawnIntervalCounter = this.spawnInterval * FPS; // Ensure this is set correctly

        //System.out.println("Balls to spawn: " + ballsToSpawn);

         // Balls from config file
        int spawnIntervalCounter = spawnInterval * FPS;

        activeBalls = new ArrayList<>();
        wallsList = new ArrayList<>();

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
        loadLevel(layout);

        initializeBallColorMap();
        if (board == null) {

            //println("Failed to load the board");
        } else {
            //println("Board loaded successfully");
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
        if (mouseButton == PConstants.LEFT) { // Left mouse button
            // Check if Ctrl key is pressed
            if (keyPressed && key == PConstants.CONTROL) {
                // Ctrl + Left Click: Remove the line under the mouse
                removeLineAtMouse();
            } else {
                // Create a new player-drawn line with color black and thickness 10
                currentLine = new PlayerLine(Color.BLACK, 10);
                // Add the initial point to the line
                currentLine.addSegment(mouseX, mouseY);
            }
        if(mouseButton == PConstants.RIGHT){
            removeLineAtMouse();
        }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseButton == PConstants.LEFT && currentLine != null) {
            // Add line segments as the mouse is dragged
            currentLine.addSegment(mouseX, mouseY);
        }

        // Right mouse button or Ctrl + Left click for removing lines
        if (mouseButton == PConstants.RIGHT || (mouseButton == PConstants.LEFT && keyPressed && key == PConstants.CONTROL)) {
            removeLineAtMouse();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouseButton == PConstants.LEFT && currentLine != null) {
            // Add the drawn line to the list of lines and reset the current line
            playerLines.add(currentLine);
            currentLine = null;
        }
    }

    // Helper function to remove a line if mouse is near it
    private void removeLineAtMouse() {
        // Check if the mouse position collides with any player-drawn line
        for (int i = playerLines.size() - 1; i >= 0; i--) {
            PlayerLine line = playerLines.get(i);
            if (line.intersects(mouseX, mouseY)) {
                // Remove the line if collision occurs
                playerLines.remove(i);
                break;
            }
        }
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
        resetOccupied();
        drawBoard();
        spawnIntervalCounterModifier();
        BallMovement();
        displayLine();
        

         

        
        
        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        text("Score: ", WIDTH - 150, 30); 
        
        //----------------------------------
        //display time
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------
		//display game end message

        displayTime();

    }

    char[][] board;

    public void loadLevel(String filepath) {
    board = new char[BOARD_WIDTH][BOARD_HEIGHT];
    spawners = new ArrayList<>();
    String[] lines = loadStrings(filepath);

    for (int y = 0; y < lines.length; y++) {
        for (int x = 0; x < lines[y].length(); x++) {
            char tileType = lines[y].charAt(x);
            board[x][y+2] = tileType;
            
            if (tileType == 'S') {
                spawners.add(new int[]{x, y + 2});
            }

            if (tileType == 'X' || (tileType >= '1' && tileType <= '4')) {
                wallsList.add(new Wall(x, (y + 2), tileType));
            }
        }
    }
    //System.out.println("Spawners: " + spawners);
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
                    if(!occupiedBall[x][y]){
                    image(walls[0], x * CELLSIZE, y * CELLHEIGHT);
                    }
                    else{
                        image(tile, x * CELLSIZE, y * CELLHEIGHT);
                    }
                    //println("x");
                    break;
                case '1':
                    if(!occupiedBall[x][y]){
                    image(walls[1], x * CELLSIZE, y * CELLHEIGHT);
                    }
                    else{
                        image(tile, x * CELLSIZE, y * CELLHEIGHT);
                    }
                    //println("1");
                    break;
                case '2':
                    if(!occupiedBall[x][y]){
                    image(walls[2], x * CELLSIZE, y * CELLHEIGHT);
                    }
                    else{
                        image(tile, x * CELLSIZE, y * CELLHEIGHT);
                    }
                    //println("2");
                    break;
                case '3':
                    
                    if(!occupiedBall[x][y]){
                    image(walls[3], x * CELLSIZE, y * CELLHEIGHT);
                    }
                    else{
                        image(tile, x * CELLSIZE, y * CELLHEIGHT);
                    }
                    //println("3");
                    break;
                case '4':
                    if(!occupiedBall[x][y]){
                    image(walls[4], x * CELLSIZE, y * CELLHEIGHT);
                    }
                    else{
                        image(tile, x * CELLSIZE, y * CELLHEIGHT);
                    }
                    //println("4");
                    break;
                case 'S':
                    image(spawner, x * CELLSIZE, y * CELLHEIGHT);
                    //println("S");
                    break;
                case 'B':
                    // Check for balls and their color
                    if (x + 1 < BOARD_WIDTH && Character.isDigit(board[x + 1][y])) {
                        String ballId = String.valueOf(tileType) + board[x + 1][y]; // e.g., "B0", "B1"
                        int ballType = Character.getNumericValue(board[x + 1][y]);
                        String ballColor = ballColorMap.get(ballId);
                        if (ballId != null && !occupiedBall[x][y]) {
                            spawnBallFromBoard(ballColor, x, y);
                            occupiedBall[x][y] = true;
                            occupiedBall[x+1][y]= true;
                        }
                        if(occupiedBall[x][y]){
                            image(tile, x * CELLSIZE, y * CELLHEIGHT);
                        }
                        //println(ballColor);
                    }
                    break;
                case 'H':
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
                        //println("hole"+ holeType);
                        // Since the hole occupies two cells, skip the next x
                        x++; 
                    } else {
                        image(tile, x * CELLSIZE, y * CELLHEIGHT); // Empty tile
                    }
                    break;
                default:
                    image(tile, x * CELLSIZE, y * CELLHEIGHT);
                    break;
            }
        }
    }
}

public void spawnBallFromSpawner(String ballId) {
    if (spawners.isEmpty()) return; // No spawners found

    // Randomly select a spawner
    int[] spawnerPos = spawners.get(random.nextInt(spawners.size()));
    int x = spawnerPos[0] * CELLSIZE;
    int y = spawnerPos[1] * CELLHEIGHT;
    Ball newBall = new Ball(x, y, ballId, balls); // Pass the balls array
    activeBalls.add(newBall);
    //System.out.println("Spawned ball at: (" + x + ", " + y + ") with color: " + ballId);
}

public void spawnBallFromBoard(String ballId, int xCor, int yCor){
    xCor = xCor * CELLSIZE;
    yCor = yCor * CELLHEIGHT;
    Ball newBall = new Ball(xCor, yCor, ballId, balls);
    activeBalls.add(newBall);
}

private void resetOccupied() {
    for (int y = 0; y < BOARD_HEIGHT; y++) {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            occupied[x][y] = false;  // Reset the occupied flag
        }
    }
}

public void spawnIntervalCounterModifier(){
    if (spawnIntervalCounter > 0) {
            spawnIntervalCounter--;
        } else if (!ballsToSpawn.isEmpty()) {
            // Spawn next ball
            String nextBall = ballsToSpawn.remove(0); // Get the next ball
            spawnBallFromSpawner(nextBall);
            
            // Reset the interval counter
            spawnIntervalCounter = spawnInterval * FPS;
        }
}

private void BallMovement() {
    for (Ball ball : activeBalls) {
        ball.update();
        ball.display(this); // Render the ball
        for (Wall wall : wallsList) {
            //System.out.println("Colliision check called");
            wall.checkCollision(ball); // Check collision and handle response
        }
    }
}

public void displayLine(){
    for (PlayerLine line : playerLines) {
            line.display(this);
        }
}

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
