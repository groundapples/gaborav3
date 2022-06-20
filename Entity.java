import java.awt.Graphics;
import java.awt.Color;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// possible exceptions
import java.io.IOException;

/**
 * Desc: Entities encompass every non-bullet physical object in the game
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Entity {
    private int team;
    private double x;
    private double y;
    private double xSpeed = 0;
    private double ySpeed = 0;
    private double xAccel = 0;
    private double yAccel = 0;
    private int length;
    private int width;
    private double gravity;
    private String direction;
    private String picName;
    private String type = "Entity";
    private boolean touchable;
    private Entity interactingWith;
    // the animation row and column
    private int row;
    private int col;
    // ----------------------------
    private ArrayList<ArrayList<BufferedImage>> frames;
    private long animationTime = System.currentTimeMillis();

    Entity(int x, int y, int length, int width, String picName) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.width = width;
        this.xSpeed = 0;
        this.ySpeed = 0;
        this.direction = "right";
        this.gravity = 0;
        this.picName = picName;
        this.row = 0;
        this.col = 0;
        this.touchable = true;
        loadImages();
    }

    // load the entity's images
    public void loadImages() {
        if (!picName.equals("")) {
            frames = new ArrayList<ArrayList<BufferedImage>>();
            for (int row = 0; row < 4; row++) {
                boolean moreCols = true;
                int col = 0;
                // while there are still more columns to be loaded, continue loading
                while (moreCols) {
                    try {
                        frames.add(new ArrayList<BufferedImage>());
                        frames.get(row).add(ImageIO.read(new File("images/" + picName + row + "-" + col + ".png")));
                        col++;
                    } catch (IOException ex) {
                        moreCols = false;
                    }
                }
            }
            row = 0;
            col = 0;
        }
    }

    // this method will draw the entity at the correct position on the screen
    public void draw(Graphics g, int xRange, int yRange, SlowmoTracker slowmoTracker) {
        // if the entity is visible on the screen, then draw it
        // this is done to save resources
        if (checkInRange(xRange, yRange)) {
            // reverse the image if the entity is looking the other way
            if (this.direction.equals("right")) {
                g.drawImage(this.frames.get(row).get(col), (int) this.x - xRange, (int) this.y - yRange, null);
            } else {
                g.drawImage(this.frames.get(row).get(col), (int) this.x + this.length - xRange,
                        (int) this.y - yRange,
                        -length, width, null);
            }
        }
    }

    // update the entity's x and y speed
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        this.xSpeed += this.xAccel;
        this.ySpeed += this.yAccel + this.gravity * slowmoTracker.getActiveSlowAmount();
    }

    // check if the entity is on the screen
    public boolean checkInRange(int xRange, int yRange) {
        int centerEntityX = xRange + Const.WIDTH / 2;
        int centerEntityY = yRange + Const.HEIGHT / 2;

        if (distance(centerEntityX, centerEntityY, this.x + length / 2,
                this.y + width / 2) < length / 2 + Const.WIDTH / 2 + 100) {
            return true;
        }
        return false;
    }

    // this method checks if 2 rectangles are touching, specifically 2 entities
    public boolean rectRectDetect(Entity rect, Entity rect2) {
        double leftSide = rect.x;
        double rightSide = rect.x + rect.length;
        double topSide = rect.y;
        double botSide = rect.y + rect.width;
        if (rect2.x + rect2.length > leftSide && rect2.x < rightSide && rect2.y + rect2.width > topSide
                && rect2.y < botSide) {
            return true;
        }
        return false;
    }

    // this uses method overloading. The extra range parameter adds a buffer area
    // between the rectangles. The larger the range, the farther apart the entities
    // can be and still be considered 'touching'
    public boolean rectRectDetect(Entity rect, Entity rect2, int range) {
        double leftSide = rect.x - range;
        double rightSide = rect.x + rect.length + range;
        double topSide = rect.y - range;
        double botSide = rect.y + rect.width + range;
        if (rect2.x + rect2.length > leftSide && rect2.x < rightSide && rect2.y + rect2.width > topSide
                && rect2.y < botSide) {
            return true;
        }
        return false;
    }

    // check if the entity is within interacting range of any other entities
    public void checkInteract(ArrayList<Entity> entities, Map map, Music music) {
        for (int i = 0; i < entities.size(); i++) {
            // if the entity is within range to interact, trigger the interaction
            if (rectRectDetect(this, entities.get(i), 50)) {
                entities.get(i).interact(this, map, entities, music);
            }
        }
    }

    // method overloading. This is a simpler version that is used by enemies
    public void checkInteract(ArrayList<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            if (rectRectDetect(this, entities.get(i), 50)) {
                entities.get(i).interact(this, entities);
            }
        }
    }

    // method finds the distance between 2 points
    public double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    // generates a random number between min and max integers
    public int randint(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    // This method will trigger when this entity is interacted WITH. By default,
    // nothing happens
    public void interact(Entity interactor, Map map, ArrayList<Entity> entities, Music music) {
    }

    // method overloading. This is a simpler version used by doors.
    public void interact(Entity interactor, ArrayList<Entity> entities) {
    }

    // this will deal damage to the entity. By default, entities do not take damage
    public void takeDamage(double damage) {
    }

    // causes the entity to jump. By default, entities do not jump
    public void jump() {
    }

    // by default, entities do not move.
    public void move(String dir) {
    }

    // // by default, entities do not attack (used by enemy and player)
    public void attack(int aimX, int aimY, ArrayList<Entity> entities, ArrayList<Bullet> bullets,
            SlowmoTracker slowmoTracker) {

    }

    // by default, entities do not dash (Used by player)
    public void dash(double aimX, double aimY, ArrayList<Entity> entities, ArrayList<Bullet> bullets,
            SlowmoTracker slowmoTracker) {

    }

    // used by the player to purchase items. By default, nothing happens
    public void mouseInteract(int x, int y) {

    }

    // this triggers when entities get kills. By default, nothing happens. Mostly
    // used by the player
    public void getKill() {

    }

    // removes this entity from an arraylist of entities
    public void removeThis(ArrayList<Entity> entities) {
        entities.remove(this);
    }

    // getters
    public String getType() {
        return type;
    }

    public double getGravity() {
        return gravity;
    }

    public String getPicName() {
        return this.picName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getLength() {
        return this.length;
    }

    public int getWidth() {
        return this.width;
    }

    public ArrayList<ArrayList<BufferedImage>> getFrames() {
        return frames;
    }

    public String getDirection() {
        return direction;
    }

    public double getXSpeed() {
        return this.xSpeed;
    }

    public double getYSpeed() {
        return this.ySpeed;
    }

    public int getTeam() {
        return this.team;
    }

    public boolean getTouchable() {
        return touchable;
    }

    public Entity getInteractingWith() {
        return this.interactingWith;
    }

    public ArrayList<Item> getItems() {
        return null;
    }

    public double getHp() {
        return 100;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public long getAnimationTime() {
        return animationTime;
    }

    // setters
    public void setType(String type) {
        this.type = type;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public void setXAccel(double accel) {
        this.xAccel = accel;
    }

    public void setYAccel(double accel) {
        this.yAccel = accel;
    }

    public void setYSpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    public void setXSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setFrames(ArrayList<ArrayList<BufferedImage>> frames) {
        this.frames = frames;
    }

    public void setFrames(int row, int col, BufferedImage image) {
        this.frames.get(row).set(col, image);
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void setInteractingWith(Entity entity) {
        interactingWith = entity;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setAnimationTime(long animationTime) {
        this.animationTime = animationTime;
    }
}
