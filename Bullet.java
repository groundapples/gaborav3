import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// possible exceptions
import java.io.IOException;

/**
 * Desc: Bullets are circular objects that are able to harm entities and are the
 * main way of interaction between player and enemies
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Bullet {
    private double x;
    private double y;
    private double aimX, aimY; // the position the bullet must travel towards
    private double startX, startY; // the position at which the bullet started
    private double angle; // the angle, in relation to straight up, that the bullet is travelling in
    private int r;
    private double speed;
    private int team; // the team of the entity that shot the bullet
    private int damage; // damage the bullet will deal
    private int bulletRange; // distance the bullet will travel before disappearing
    private boolean isRemovedOnHit; // is the bullet removed upon hitting something?
    private BufferedImage original;
    private BufferedImage picture;
    private Entity shooter; // the entity that shot the bullet
    private BulletSound sound;
    private boolean deflectable; // can the bullet be deflected by a slash

    Bullet(double x, double y, double aimX, double aimY, int r, double speed, int team,
            int damage, int bulletRange, boolean isRemovedOnHit, String picName, Entity shooter) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.aimX = aimX - x;
        this.aimY = aimY - y;
        this.angle = getAngle();
        this.r = r;
        this.speed = speed;
        this.team = team;
        this.damage = damage;
        this.bulletRange = bulletRange;
        this.isRemovedOnHit = isRemovedOnHit;
        this.shooter = shooter;
        this.deflectable = true;
        this.sound = new BulletSound("audio/gunshot.wav");

        // load the bullet's image
        try {
            this.original = ImageIO.read(new File("images/" + picName + ".png"));
        } catch (IOException ex) {
            System.out.println("Image not found");
        }
        // rotate the bullet's image to the correct angle
        this.picture = rotateImage(this.original, this.angle);
    }

    // this method will update the bullet (move it, check for collision and
    // interactions, etc)
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        // play the sound of the bullet being fired
        if (!sound.getPlayed()) {
            sound.attackSound();
        }

        // move the bullet in the correct direction
        // make sure to normalize the travel distance and set that distance to the speed
        // of the bullet
        // this is to insure the bullet will only travel at a set speed
        this.x = this.x + this.speed * slowmoTracker.getActiveSlowAmount() * aimX
                / Math.sqrt(Math.pow(this.aimX, 2) + Math.pow(this.aimY, 2));
        this.y = this.y + this.speed * slowmoTracker.getActiveSlowAmount() * this.aimY
                / Math.sqrt(Math.pow(this.aimX, 2) + Math.pow(this.aimY, 2));

        // check for collisions with entities
        for (int i = 0; i < entities.size() && this != null; i++) {
            // if the entity is not touchable, it will not count it as an interaction
            if (circRectDetect(this, entities.get(i)) && this.team != entities.get(i).getTeam() &&
                    entities.get(i).getTouchable()) {

                // when bullets are NOT removed on hit, they tend to do way more damage than
                // they are supposed to when time is slowed. This will make them do the correct
                // amount of damage during time slow
                if (!this.isRemovedOnHit) {
                    entities.get(i).takeDamage(this.damage * slowmoTracker.getActiveSlowAmount());
                } else {
                    // otherwise, do normal damage
                    entities.get(i).takeDamage(this.damage);
                }

                // if the entity that just took damage has 0 or less health, give a kill to the
                // shooter.
                if (entities.get(i).getHp() <= 0 && this.shooter != null) {
                    this.shooter.getKill();
                }

                // remove the bullet if it is removed on hit
                if (this.isRemovedOnHit) {
                    remove(bullets);
                }
            }
        }

        if (this != null) {
            // if the bullet has travelled farther than it's max distance, remove it
            if (distance(this.x, this.y, this.startX, this.startY) > this.bulletRange) {
                remove(bullets);
            }
        }
    }

    // this method will remove this object from an arraylist of bullets
    public void remove(ArrayList<Bullet> bullets) {
        bullets.remove(this);
    }

    // find the angle at which the bullet is travelling at
    public double getAngle() {
        // Where the object starts
        double startLocationX = this.x + this.r / 2;
        double startLocationY = this.y + this.r / 2;

        // Where the object is travelling towards
        double touchX = this.aimX + x;
        double touchY = this.aimY + y;

        double theta = 180.0 / Math.PI * Math.atan2(startLocationX - touchX, startLocationY - touchY);

        return theta;
    }

    // this method will draw the bullet at the correct location in relation to the
    // player (xRange, yRange)
    public void draw(Graphics g, int xRange, int yRange) {
        g.drawImage(this.picture, (int) this.x - xRange - this.r / 2, (int) this.y - yRange - this.r / 2, null);
    }

    // This method will check if a circle and a rectangle are touching each other
    // Specifically, it checks between bullets and entities.
    public boolean circRectDetect(Bullet circle, Entity rect) {
        double leftSide = rect.getX();
        double rightSide = rect.getX() + rect.getLength();
        double topSide = rect.getY();
        double botSide = rect.getY() + rect.getWidth();
        if (circle.x + circle.r / 2 > leftSide && circle.x - circle.r / 2 < rightSide
                && circle.y + circle.r / 2 > topSide
                && circle.y - circle.r / 2 < botSide) {
            return true;
        }
        return false;
    }

    // this method will check if a circle and a circle are touching each other
    // specifically checks between 2 bullets
    public boolean circCircDetect(Bullet circle, Bullet circle2) {
        if (distance(circle.x, circle.y, circle2.x, circle2.y) < circle.r / 2 + circle2.r / 2) {
            return true;
        }
        return false;
    }

    // this method will find the distance between two points
    public double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    // This method will rotate an image to the correct angle
    private BufferedImage rotateImage(BufferedImage image, double angle) {
        BufferedImage rotatedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.rotate(Math.toRadians(-this.angle), image.getWidth() / 2, image.getHeight() / 2);
        g2d.drawImage(image, null, 0, 0);
        g2d.dispose();
        return rotatedImage;
    }

    // setters
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setAim(double x, double y) {
        this.aimX = x;
        this.aimY = y;
        this.angle = getAngle();
        this.picture = rotateImage(this.original, this.angle);
    }

    public void setShooter(Entity shooter) {
        this.shooter = shooter;
    }

    public void setSound(String soundName) {
        this.sound.setShotSound(soundName);
    }

    public void setDeflectable(boolean deflectable) {
        this.deflectable = deflectable;
    }

    // getters
    public double getSpeed() {
        return this.speed;
    }

    public int getTeam() {
        return this.team;
    }

    public double getAimX() {
        return aimX;
    }

    public double getAimY() {
        return aimY;
    }

    public Entity getShooter() {
        return shooter;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean getDeflectable() {
        return this.deflectable;
    }
}
