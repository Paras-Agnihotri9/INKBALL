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
    public static int score = 0;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();

    private HashMap<String, String> ballColorMap;

    public boolean flag = true;

    private PlayerLine currentLine; // Current line being drawn

    private ArrayList<PlayerLine> playerLines; // All player-drawn lines

    //List<Hole> holesList; // List to hold holes

    List<int[]> spawners;

    int time;
    int spawnInterval;
    String layout;
    List<String> ballsToSpawn;
    public static float scoreIncreaseModifier;
    public static float scoreDecreaseModifier;
    int spawnIntervalCounter;
    List<Ball> activeBalls; // List to hold active balls
    List<Wall> wallsList;
    List<Hole> holesList;
    int startTime;
    int currentLevelIndex = 0;

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
        holesList = new ArrayList<>();
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

        initializeBallColorMap();
        if (board == null) {
            println("Failed to load the board");
        } else {
            println("Board loaded successfully");
        }

        setupLevel(currentLevelIndex);
        }

    /**
     * Receive key pressed signal from the keyboard.
     */
    private void setupLevel(int levelIndex) {
        GameConfig gameConfig = new GameConfig(this, "config.json");
        this.time = gameConfig.getTime(levelIndex);
        this.spawnInterval = gameConfig.getSpawnInterval(levelIndex);
        this.layout = gameConfig.getLayout(levelIndex);
        this.ballsToSpawn = gameConfig.getBalls(levelIndex);
        this.scoreIncreaseModifier = gameConfig.getScoreIncreaseModifier(levelIndex);
        this.scoreDecreaseModifier = gameConfig.getScoreDecreaseModifier(levelIndex);
        this.spawnIntervalCounter = this.spawnInterval * FPS;
        // Load the level layout
        loadLevel(layout);
    }

    private void checkLevelCompletion() {
        if (allBallsAbsorbed()) {
            currentLevelIndex++; // Move to the next level
            setupLevel(currentLevelIndex);
        } else if (timeReached()) {
            // Stop displaying timer and score
            displayGameEndMessage();
        }
    }

    private boolean allBallsAbsorbed() {
        return ballsToSpawn.isEmpty() && activeBalls.isEmpty();
    }

    private boolean timeReached() {
        long elapsedTime = (millis() - startTime) / 1000; // Calculate elapsed time in seconds
        return elapsedTime >= time;
    }

    private void displayGameEndMessage() {
        fill(0);
        textAlign(CENTER);
        textSize(32);
        text("ENDED", WIDTH / 2, HEIGHT / 2);
    }


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
            if (keyPressed && key == PConstants.CONTROL) {
                // Ctrl + Left Click: Remove the line under the mouse
                removeLineAtMouse();
            } else {
                // Start drawing a new line
                currentLine = new PlayerLine(Color.BLACK, 10);
                currentLine.addSegment(mouseX, mouseY); // Initial point
            }
        } else if (mouseButton == PConstants.RIGHT) {
            // Right click: Remove the line under the mouse
            removeLineAtMouse();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseButton == PConstants.LEFT && currentLine != null) {
            // Add segments as the mouse is dragged
            currentLine.addSegment(mouseX, mouseY);
        }

        // Handle right click or Ctrl+Left click for line removal while dragging
        if (mouseButton == PConstants.RIGHT || (mouseButton == PConstants.LEFT && keyPressed && key == PConstants.CONTROL)) {
            removeLineAtMouse();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouseButton == PConstants.LEFT && currentLine != null) {
            // Mark the line as complete and add to the list of player-drawn lines
            currentLine.setComplete(true);
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
        background(255);
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
        
        checkLevelCompletion();
         

        
        
        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        text("Score: " + score, WIDTH - 150, 40); 
        
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
                // Store the tile type in the board, ensuring we don't go out of bounds
                if (y + 2 < BOARD_HEIGHT) {
                    board[x][y + 2] = tileType;
                }
                
                // Check for spawners
                if (tileType == 'S') {
                    spawners.add(new int[]{x, y + 2});
                }
                if (tileType == 'H') {
                    // Check if there's a character after 'H' and it's a digit
                    if (x + 1 < lines[y].length() && Character.isDigit(lines[y].charAt(x + 1))) {
                        char holeType = lines[y].charAt(x + 1);
                        System.out.println(" x " + x + " y " + y + " type: " + holeType);
                        holesList.add(new Hole(x, y + 2, holeType));
                    } else {
                        System.out.println("Error: Expected a number after 'H' but found none or invalid.");
                    }
                }

                // Check for walls, while skipping balls and holes
                if (tileType == 'X' || (tileType >= '1' && tileType <= '4')) {
                    // If the previous tile is not a ball or hole, add to wallsList
                    if (tileType != 'X') {
                        if (x > 0 && (board[x - 1][y + 2] != 'B' && board[x - 1][y + 2] != 'H')) {
                            wallsList.add(new Wall(x, (y + 2), tileType));
                            System.out.println("New wall added "+ tileType + "at" + x + " "+ y);
                        }
                    } else {
                        // Add 'X' walls directly
                        wallsList.add(new Wall(x, (y + 2), tileType));
                    }
                }
            }
        }
    }


     private void displayTime() {
        long elapsedTime = (millis() - startTime) / 1000; // Calculate elapsed time in seconds

        String timeString = String.valueOf(elapsedTime); // Convert to String

        // Display the time on the right side of the top bar
        fill(0);
        text("Time: " + timeString, WIDTH - 150, TOPBAR / 2 + 25); // Adjust the position as needed
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

    public void spawnIntervalCounterModifier() {
    // Count down the spawn interval
        if (spawnIntervalCounter > 0) {
            spawnIntervalCounter--;
        } else if (!ballsToSpawn.isEmpty()) {
            // Spawn the next ball after the interval elapses
            String nextBall = ballsToSpawn.remove(0); // Get the next ball
            spawnBallFromSpawner(nextBall);
            
            // Reset the interval counter
            spawnIntervalCounter = spawnInterval * FPS;
        }
    }


    private void BallMovement() {
        // Temporary list to store lines and balls to be removed after the loops
        List<PlayerLine> linesToRemove = new ArrayList<>();
        List<Ball> ballsToRemove = new ArrayList<>(); // List to keep track of balls that need to be removed

        for (Ball ball : activeBalls) {
            ball.update();
            ball.display(this); // Render the ball
            ball.resetCollision();

            // Handle wall and hole collisions
            for (Wall wall : wallsList) {
                wall.handleCollision(ball);
            }

            for (Hole hole : holesList) {
                ball.attractToHole(hole); // Check attraction for each ball
                if (ball.captured) {
                    if (ball.isCaptureSuccessfulFlag) {
                        ballsToRemove.add(ball); // Mark ball for removal if captured successfully
                    } else {
                        ballsToSpawn.add(ball.getBallId()); // Add back to spawn if absorption failed
                    }
                }
            }

            // Check for player line collisions
            for (PlayerLine line : playerLines) {
                if (line.isComplete()) {
                    line.checkCollisionWithBall(ball);
                    if (ball.hasCollided()) {
                        linesToRemove.add(line); // Mark the line for removal if collided
                    }
                }
            }
        }

        // Remove absorbed balls and lines
        activeBalls.removeAll(ballsToRemove);
        playerLines.removeAll(linesToRemove);

        // Do not handle spawning here anymore; rely on spawnIntervalCounterModifier to handle spawning
    }


        public void displayLine(){
            for (PlayerLine line : playerLines) {
            line.display(this); // Display each complete line
            }
        // If there's a line being drawn (currentLine), render it as well
            if (currentLine != null) {
                currentLine.display(this); // Display the current line being drawn
            }
        }

        public void removeLine(PlayerLine line) {
            playerLines.remove(line);
        }

        public void checkBallCollisions() {
        // Iterate over active balls and check for collisions with each line
        for (Ball ball : activeBalls) {
            // Iterate over lines and check for collisions
            for (int i = playerLines.size() - 1; i >= 0; i--) {
                PlayerLine line = playerLines.get(i);
                if (line.checkCollisionWithBall(ball)) {
                    playerLines.remove(i); // Remove the line if a collision occurred
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
