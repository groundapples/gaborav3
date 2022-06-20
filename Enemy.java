import java.util.*;

/**
 * Desc: Enemies are extensions of creatures, but encompass all enemy npcs that
 * are trying to kill the player. This is an abstract class.
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

abstract class Enemy extends Creature {
    // target destination is the location of whatever the enemy is chasing after (in
    // most cases it is the player)
    private double targetDestinationX = 0;
    private double targetDestinationY = 0;
    // how close does the player have to be to be detected?
    private int detectRange = 0;
    // how close does the enemy stop before attacking?
    private int engageRange = 0;

    Enemy(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setType("Enemy");
    }

    // this method will trigger an attack against the player
    abstract void attack(ArrayList<Entity> entities, ArrayList<Bullet> bullets);

    // this method will update the destination that the enemy is heading towards
    public void updateDestination(ArrayList<Entity> entities) {
        // if the player is within detect range, then update the destination
        if (distance(super.getX(), super.getY(), entities.get(0).getX(),
                entities.get(0).getY()) < detectRange) {
            setDestinationX(entities.get(0).getX() + entities.get(0).getLength() / 2);
            setDestinationY(entities.get(0).getY() + entities.get(0).getWidth() / 2);
        }
    }

    // this method will cause the enemy to travel in the direction of the player and
    // attack when in range
    public void search(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        // check if the enemy has detected the player
        if (this.getDestinationX() != 0 && this.getDestinationY() != 0) {
            // check if the distance between the player and the enemy is less than the
            // enemy's engage range. If so, initiate an attack
            if (super.distance(super.getX() + super.getLength() / 2, super.getY() + super.getWidth() / 2,
                    this.getDestinationX(), this.getDestinationY()) < this.getEngageRange()) {
                // check if the enemy's attack is off cooldown
                if (super.getCanAttack()) {
                    attack(entities, bullets);
                    super.setCanAttack(false);
                    super.setLastAttack(slowmoTracker.getGameTime());
                }
                // stop the enemy from moving while attacking
                super.setXAccel(0);
                super.setYAccel(0);
            }
            // if the enemy cannot approach the player further, cause them to jump over the
            // obstacle in their way
            else if (super.getXSpeed() == 0 && super.getJumps() > 0) {
                super.setYAccel(-super.getJumpSpeed());
                super.setJumps(super.getJumps() - 1);
                // open any doors that are in between the player and the enemy
                super.checkInteract(entities);
            }
            // move the enemy in the x direction of the player
            else if (super.getX() + super.getLength() / 2 > this.getDestinationX()) {
                super.setXAccel(-super.getRunAccel());
                super.setYAccel(0);
                super.setDirection("left");
            } else if (super.getX() + super.getLength() / 2 < this.getDestinationX()) {
                super.setXAccel(super.getRunAccel());
                super.setYAccel(0);
                super.setDirection("right");
            }
        }
    }

    // Setters
    public void setDetectRange(int range) {
        this.detectRange = range;
    }

    public void setEngageRange(int range) {
        this.engageRange = range;
    }

    public void setDestinationX(double x) {
        this.targetDestinationX = x;
    }

    public void setDestinationY(double y) {
        this.targetDestinationY = y;
    }

    // Getters
    public double getDestinationX() {
        return this.targetDestinationX;
    }

    public double getDestinationY() {
        return this.targetDestinationY;
    }

    public int getDetectRange() {
        return this.detectRange;
    }

    public int getEngageRange() {
        return this.engageRange;
    }

}
