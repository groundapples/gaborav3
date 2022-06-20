import java.awt.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// possible exceptions
import java.io.IOException;

/**
 * Desc: Map controls where the walls and enemies spawn as well as room
 * locations
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Map {
    private ArrayList<Room> rooms;
    private boolean mapLoading = false;
    private boolean loadingScreenDrawn = false;
    private int numberOfEnemies = 2;
    private BufferedImage loadingImage;

    Map(int x, int y, ArrayList<Entity> entities, int mapSize) {
        rooms = new ArrayList<Room>();
        // load the loading page image
        try {
            loadingImage = ImageIO.read(new File("images/backgrounds/loadingPage.png"));
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("failed to load wall");
        }

        loadMapFile(entities, x, y, mapSize);
    }

    // this will load the map with the amount of rooms required
    // this will spawn in enemies as well

    // A basic summary of how this method works: it will take a random room layout
    // and check to see if the room fits into the map (does not overlap any other
    // room). If it does, then it will spawn the room in. If it doesn't, then it
    // will try again with a different room. If a certain amount of tries passes,
    // then it will block off the exit.
    public void loadMapFile(ArrayList<Entity> entities, int x, int y, int mapSize) {
        int numberOfCustomRooms = 0; // this variable will keep track of the number of unique rooms in the rooms.txt
                                     // file

        // Load the rooms.txt file
        File mapFile = new File("");
        Scanner mapScanner = new Scanner("");
        try {
            mapFile = new File("rooms.txt");
            mapScanner = new Scanner(mapFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        // find the number of custom rooms. Each unique room is divided with ---
        mapScanner = readRoom("rooms.txt", 0);
        while (mapScanner.hasNext()) {
            if (mapScanner.nextLine().equals("---")) {
                numberOfCustomRooms++;
            }
        }

        // the x and y positions that the map is currently building at
        int xPos = x;
        int yPos = y;
        // the number of lines from the beginning of the room
        int lineNum = 0;
        // the starting x and y coordinates of the entrance to the room
        int startX = -1;
        int startY = -1;
        // arraylists to keep track of exit locations
        ArrayList<Integer> exitX = new ArrayList<Integer>();
        ArrayList<Integer> exitY = new ArrayList<Integer>();
        // the length and width of the room
        int roomLength = 0;
        int roomWidth = 0;
        // the number of rooms already created
        int roomsSpawned = 0;
        // the different types of walls
        String[] wallTypes = { "tiledWall", "woodenWall", "stoneWall", "cobbleWall", "brickWall" };

        // this loop will make rooms until it has reached the amount specified
        while (roomsSpawned < mapSize) {
            roomsSpawned++;

            // Take a random room to spawn
            int roomToSpawn = randint(0, numberOfCustomRooms - 1);
            // take a random wall type to spawn
            String wallTypeToSpawn = wallTypes[randint(0, wallTypes.length - 1)];
            // a boolean checking whether or not the room fits (does not overlap other
            // rooms)
            boolean roomFits = false;
            // a boolean that marks whether or not to load in the physical walls of the room
            boolean loadRoom = true;
            int timesTriedToLoad = 0;
            // Create a default room
            rooms.add(new Room());
            do {
                // load in the room that it is trying to spawn
                mapScanner = readRoom("rooms.txt", roomToSpawn);
                loadRoom = true;
                while (loadRoom) {
                    String text = mapScanner.nextLine();
                    // when the scanner reads ---, that means that the room layout has ended and so
                    // it should stop loading
                    if (text.equals("---")) {
                        loadRoom = false;
                    } else {
                        // each space in the rooms.txt file is 50 pixels in length
                        // this measures the total length of the room
                        roomLength = text.length() * 50;
                        // loop through the row
                        for (int i = 0; i < text.length(); i++) {
                            String subsection = text.substring(i, i + 1);
                            // find the entrance to the room.
                            if (subsection.equals("S")) {
                                startX = xPos + i * 50;
                                startY = lineNum * 50;
                            }
                        }
                        // when the scanner moves to read the next line, it will be 50 pixels down
                        yPos += 50;
                        lineNum++;
                    }
                    // this runs after the scanner has read the entire room. Thus, the width of the
                    // room would be the number of lines it has read times 50
                    roomWidth = lineNum * 50;
                }

                // load the room it is trying to spawn
                mapScanner = readRoom("rooms.txt", roomToSpawn);
                loadRoom = true;
                lineNum = 0;
                // get the starting location of the room. (the top left corner)
                if (!exitX.isEmpty() && !exitY.isEmpty()) {
                    yPos = exitY.get(0) - startY;
                    xPos = exitX.get(0) + 50;
                } else {
                    xPos = x;
                    yPos = y;
                }
                // Adjust the room to the correct size
                rooms.get(rooms.size() - 1).setX(xPos);
                rooms.get(rooms.size() - 1).setY(yPos);
                rooms.get(rooms.size() - 1).setLength(roomLength);
                rooms.get(rooms.size() - 1).setWidth(roomWidth);

                // see if the room does not overlap any other rooms
                if (rooms.get(rooms.size() - 1).overlaps(rooms)) {
                    roomFits = false;
                    // select a different room if it doesnt fit
                    roomToSpawn = randint(0, numberOfCustomRooms - 1);
                    timesTriedToLoad++;
                } else {
                    // if it fits, spawn in the walls
                    roomFits = true;
                    // remove the exit locations when the room is successfully built
                    if (!exitX.isEmpty()) {
                        exitX.remove(0);
                        exitY.remove(0);
                    }
                }
            } while (!roomFits && timesTriedToLoad < 100);

            // if the room fails to load 100 times, then block off the exits and do not
            // build the room
            if (timesTriedToLoad == 100) {
                entities.add(new Wall(exitX.get(0), exitY.get(0), 50, 50, wallTypeToSpawn));
                entities.add(new Wall(exitX.get(0), exitY.get(0) + 50, 50, 50, wallTypeToSpawn));
                removeEntityAt(exitX.get(0) + 10, exitY.get(0), entities);
                removeEntityAt(exitX.get(0) + 10, exitY.get(0) + 50, entities);
                exitX.remove(0);
                exitY.remove(0);
                rooms.remove(rooms.size() - 1);
                loadRoom = false;
            }

            // load the walls and enemies of the room if it can be built
            if (loadRoom) {
                loadWalls(xPos, yPos, mapScanner, entities, exitX, exitY, wallTypeToSpawn, roomsSpawned);
            }

        }

        // make sure to close all of the exits in the end after the max number of levels
        // have been loaded
        while (!exitX.isEmpty() && !exitY.isEmpty()) {
            String wallTypeToSpawn = wallTypes[randint(0, wallTypes.length - 1)];
            // Set the map scanner to the position where the room is in the file
            boolean roomFits = false;
            boolean loadRoom = true;
            // Create a default room
            rooms.add(new Room());
            // load in the room at the end of the level
            mapScanner = readRoom("endRooms.txt", 0);
            loadRoom = true;
            // repeat the process noted above
            while (loadRoom) {
                String text = mapScanner.nextLine();
                // load the room itself
                if (text.equals("---")) {
                    loadRoom = false;
                } else {
                    roomLength = text.length() * 50;
                    for (int i = 0; i < text.length(); i++) {
                        String subsection = text.substring(i, i + 1);
                        // find the entrance
                        if (subsection.equals("S")) {
                            startX = xPos + i * 50;
                            startY = lineNum * 50;
                        }
                    }
                    yPos += 50;
                    lineNum++;
                }
                roomWidth = lineNum * 50;
            }

            // reload the room to build the actual walls
            mapScanner = readRoom("endRooms.txt", 0);
            loadRoom = true;
            lineNum = 0;
            // find the top left corner of the room
            if (!exitX.isEmpty() && !exitY.isEmpty()) {
                yPos = exitY.get(0) - startY;
                xPos = exitX.get(0) + 50;
            } else {
                xPos = x;
                yPos = y;
            }
            // Adjust last room
            rooms.get(rooms.size() - 1).setX(xPos);
            rooms.get(rooms.size() - 1).setY(yPos);
            rooms.get(rooms.size() - 1).setLength(roomLength);
            rooms.get(rooms.size() - 1).setWidth(roomWidth);

            // check for overlaps with the other rooms
            if (rooms.get(rooms.size() - 1).overlaps(rooms)) {
                roomFits = false;
            } else {
                roomFits = true;
            }

            // block off the exit if it does not fit
            if (!roomFits) {
                entities.add(new Wall(exitX.get(0), exitY.get(0), 50, 50, wallTypeToSpawn));
                entities.add(new Wall(exitX.get(0), exitY.get(0) + 50, 50, 50, wallTypeToSpawn));
                removeEntityAt(exitX.get(0) + 10, exitY.get(0), entities);
                removeEntityAt(exitX.get(0) + 10, exitY.get(0) + 50, entities);
                exitX.remove(0);
                exitY.remove(0);
                rooms.remove(rooms.size() - 1);
                loadRoom = false;
            }
            // build the room
            else if (!exitX.isEmpty() && !exitY.isEmpty()) {
                xPos = exitX.get(0) + 50;
                yPos = exitY.get(0) - startY;
                exitX.remove(0);
                exitY.remove(0);
                mapScanner = readRoom("endRooms.txt", 0);
                loadWalls(xPos, yPos, mapScanner, entities, exitX, exitY, "tiledWall", roomsSpawned);
            }
        }

        mapScanner.close();
    }

    // This method will build all of the walls in a room
    public void loadWalls(int xPos, int yPos, Scanner mapScanner,
            ArrayList<Entity> entities, ArrayList<Integer> exitX,
            ArrayList<Integer> exitY, String wallTypeToSpawn, int roomsSpawned) {
        boolean loadRoom = true;
        // if no enemies are to be spawned in this room, turn this to true
        boolean noEnemies = false;
        while (loadRoom) {
            String text = mapScanner.nextLine();
            // when the scanner reads ---, that marks the end of the room
            if (text.equals("---")) {
                loadRoom = false;
            } else {
                // go through the string line and build the walls
                for (int i = 0; i < text.length(); i++) {
                    String subsection = text.substring(i, i + 1);
                    // make a wall when the scanner reads #
                    if (subsection.equals("#")) {
                        // this part extends a wall instead of adding another to save resources
                        if (entities.get(entities.size() - 1).getType().equals("Wall")
                                && entities.get(entities.size() - 1).getY() == yPos &&
                                (xPos + i * 50) - (entities.get(entities.size() - 1).getX()
                                        + entities.get(entities.size() - 1).getLength()) == 0) {
                            entities.get(entities.size() - 1)
                                    .setLength(entities.get(entities.size() - 1).getLength() + 50);
                        } else {
                            entities.add(new Wall(xPos + i * 50, yPos, 50, 50, wallTypeToSpawn));
                        }
                        // add a door when it reads |
                    } else if (subsection.equals("|")) {
                        entities.add(new Door(xPos + i * 50 + 10, yPos, 30, 50, "door"));
                    }
                    // add a platform
                    else if (subsection.equals("-")) {
                        entities.add(new Platform(xPos + i * 50 + 10, yPos + 20, 30, 10, "platform"));
                    }
                    // add a box
                    else if (subsection.equals("B")) {
                        entities.add(new Box(xPos + i * 50, yPos, 50, 50, "box", 10));
                    }
                    // ad an entrance
                    else if (subsection.equals("S")) {
                        if (roomsSpawned != 1) {
                            entities.add(new Door(xPos + i * 50 + 10, yPos, 30, 50, "door"));
                        } else {
                            // if it is the first room spawned, block the entrance to prevent the player
                            // from leaving the map
                            entities.add(new Wall(xPos + i * 50, yPos, 50, 50, wallTypeToSpawn));
                            entities.add(new Wall(xPos + i * 50, yPos + 50, 50, 50, wallTypeToSpawn));
                        }
                    }
                    // spawn exit door
                    else if (subsection.equals("E")) {
                        entities.add(new Door(xPos + i * 50 + 10, yPos, 30, 50, "door"));
                        exitX.add(xPos + i * 50);
                        exitY.add(yPos);
                    }
                    // spawn shop
                    else if (subsection.equals("$")) {
                        entities.add(new Shop(xPos + i * 50 + 10, yPos, 100, 100, "shop", 5));
                        noEnemies = true;
                    }
                    // spawn a lever, or end round button
                    else if (subsection.equals("L")) {
                        entities.add(new Button(xPos + i * 50 + 10, yPos, 50, 50, "lever"));
                        noEnemies = true;
                    }
                }
                // move 50 pixels down for the next row
                yPos += 50;
            }
        }

        // spawn enemies if it is not the starting room and there are enemies allowed in
        // the room
        if (roomsSpawned != 1 && !noEnemies) {
            // the min and max number of enemies in the room
            int min = 0;
            int max = 0;
            min = numberOfEnemies - 2;
            max = numberOfEnemies + 2;
            // add the enemies
            for (int i = 0; i < randint(min, max); i++) {
                addEnemy(rooms.get(rooms.size() - 1), entities);
            }
        }
    }

    // this method removes the entity at the specified location
    private boolean removeEntityAt(int x, int y, ArrayList<Entity> entities) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i).getX() == x && entities.get(i).getY() == y) {
                entities.remove(i);
                return true;
            }
        }
        return false;
    }

    // this method will add a random enemy to the room
    public void addEnemy(Room room, ArrayList<Entity> entities) {
        // get a random spawn location
        int spawnX = randint(room.getX(), room.getX() + room.getLength() - 10);
        int spawnY = randint(room.getY(), room.getY() + room.getWidth() - 10);

        // load in the enemyChances.txt file, which has the probability for each enemy
        // to be spawned
        File enemyFile = new File("");
        Scanner enemyScanner = new Scanner("");
        try {
            enemyFile = new File("enemyChances.txt");
            enemyScanner = new Scanner(enemyFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        // the number of unique enemies in the file
        int numberOfUniqueEnemies = 0;
        while (enemyScanner.hasNext()) {
            enemyScanner.nextLine();
            numberOfUniqueEnemies++;
        }

        String enemyName = null;
        while (enemyName == null) {
            // load in a random enemy
            enemyScanner = loadEnemy("enemyChances.txt", randint(0, numberOfUniqueEnemies - 1));
            // see if the chance of them spawning matches. At 100% it will always spawn.
            if (enemyScanner.nextInt() > randint(0, 100)) {
                enemyName = enemyScanner.next();
            }
        }

        // spawn in the enemy according to their name
        if (enemyName.equals("RocketGuard")) {
            entities.add(new RocketGuard(spawnX, spawnY, 34, 44, "RocketGuard/"));
        } else if (enemyName.equals("Guard")) {
            entities.add(new Guard(spawnX, spawnY, 34, 44, "Guard/"));
        } else if (enemyName.equals("SwordGuard")) {
            entities.add(new SwordGuard(spawnX, spawnY, 34, 44, "SwordGuard/"));
        }

        // this will check if the enemy is stuck inside of any other entity
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < entities.size(); i++) {
                // if it is stuck inside of another entity, it will be put to a new random
                // location until it is no longer stuck
                if (rectRectDetect(entities.get(entities.size() - 1),
                        entities.get(i)) && entities.size() - 1 != i) {
                    entities.get(entities.size() - 1).setX(randint(room.getX(), room.getX() + room.getLength() - 10));
                    entities.get(entities.size() - 1).setY(randint(room.getY(), room.getY() + room.getWidth() - 10));
                    changed = true;
                }
            }
        }
    }

    // this will load in the enemy's file
    public Scanner loadEnemy(String fileName, int enemyNumber) {
        Scanner enemyScanner = new Scanner("");
        File enemyFile;
        try {
            enemyFile = new File(fileName);
            enemyScanner = new Scanner(enemyFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        // keep track of the number of unique enemies passed
        int enemiesPassed = 0;
        while (enemiesPassed < enemyNumber) {
            enemyScanner.nextLine();
            enemiesPassed++;
        }
        return enemyScanner;
    }

    // this will read a txt file until it comes to the specified room number
    public Scanner readRoom(String fileName, int roomNumber) {
        Scanner mapScanner = new Scanner("");
        File mapFile;
        try {
            mapFile = new File(fileName);
            mapScanner = new Scanner(mapFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        // keep track of the number of unique rooms passed
        int roomsPassed = 0;
        while (roomsPassed < roomNumber) {
            String text = mapScanner.nextLine();
            if (text.equals("---")) {
                roomsPassed++;
            }
        }
        return mapScanner;
    }

    // this method will clear all rooms in the map and regenerate each room
    // it will also increase the difficulty of the map by 2
    public void recreate(int x, int y, int mapSize, ArrayList<Entity> entities) {
        rooms.clear();
        numberOfEnemies += 2;
        loadMapFile(entities, x, y, mapSize);
    }
    // this method will clear all rooms in the map and regenerate each room
    // it will also increase the difficulty of the map by 2
    public void reset(int x, int y, int mapSize, ArrayList<Entity> entities) {
        rooms.clear();
        loadMapFile(entities, x, y, mapSize);
    }

    // check if 2 entities (rectangles) are touching
    public boolean rectRectDetect(Entity rect, Entity rect2) {
        double leftSide = rect.getX();
        double rightSide = rect.getX() + rect.getLength();
        double topSide = rect.getY();
        double botSide = rect.getY() + rect.getWidth();
        if (rect2.getX() + rect2.getLength() > leftSide && rect2.getX() < rightSide &&
                rect2.getY() + rect2.getWidth() > topSide
                && rect2.getY() < botSide) {
            return true;
        }
        return false;
    }

    // generate a random integer between 2 integers
    public int randint(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    // draw the loading screen
    public void drawLoadingScreen(Graphics g) {
        g.drawImage(loadingImage, 0, 0, null);
    }

    // clear all of the rooms
    public void emptyRooms() {
        rooms.clear();
    }

    // GETTERS
    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    public boolean getMapLoading() {
        return mapLoading;
    }

    public boolean getLoadingScreenDrawn() {
        return this.loadingScreenDrawn;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    // SETTERS
    public void setMapLoading(boolean mapLoading) {
        this.mapLoading = mapLoading;
    }

    public void setLoadingScreenDrawn(boolean loadingScreenDrawn) {
        this.loadingScreenDrawn = loadingScreenDrawn;
    }

    public void setNumberOfEnemies(int numberOfEnemies) {
        this.numberOfEnemies = numberOfEnemies;
    }
}
