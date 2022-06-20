import java.awt.Graphics;
import java.awt.Color;
import java.util.*;

/**
 * Desc: Creatures are entities that can move about
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Creature extends Entity {
    private boolean invincible = false;
    private double hp;
    private double maxHp;
    private double runAccel;
    private double jumpSpeed;
    private int jumps = 1;
    private int maxJumps = 1;
    // attack variables -----------
    private int attackCooldown = 33;
    private boolean canAttack = true;
    private double lastAttack = 0;
    // ------------------------------
    private int frameUpdate;
    private int lastAnimation;
    private CreatureSound sound = new CreatureSound();

    Creature(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setType("Creature");
        super.setGravity(0.2); // gravity is 0.2 by default
        hp = 100; // 100 by default
        maxHp = hp;
        frameUpdate = 100; // update frames every 100 milliseconds
        lastAnimation = -1;
    }

    // this method will draw the health bars onto the creature
    @Override
    public void draw(Graphics g, int xRange, int yRange, SlowmoTracker slowmoTracker) {
        g.setColor(Color.black);
        g.fillRect((int) super.getX() - 2 - xRange, (int) super.getY() - 22 - yRange, super.getLength() + 4, 14);
        g.setColor(Color.green);
        g.fillRect((int) super.getX() - xRange, (int) super.getY() - 20 - yRange,
                (int) (1.0 * this.hp / this.maxHp * super.getLength()), 10);

        // draw the actual image of the creature
        super.draw(g, xRange, yRange, slowmoTracker);
    }

    // this method will move the creature in the direction specified
    // runAccel is the amount of acceleration the entity can run with
    @Override
    public void move(String dir) {
        if (dir.equals("right")) {
            super.setXAccel(runAccel);
            super.setDirection("right");
        } else if (dir.equals("left")) {
            super.setXAccel(-runAccel);
            super.setDirection("left");
        } else {
            super.setXAccel(0);
        }
    }

    // this method moves the entity upwards when they jump
    @Override
    public void jump() {
        if (this.jumps > 0) {
            this.jumps--;
            super.setYSpeed(-jumpSpeed);
        }
    }


    // this method will update the creature (move its x and y, check for
    // interactions and collisions, etc)
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        // if the creature's attack cooldown has finished, allow it to attack again.
        if ((slowmoTracker.getGameTime() - lastAttack) > attackCooldown) {
            canAttack = true;
        }

        // if the creature is moving faster than it's speed cap, thens low it down
        if (super.getXSpeed() > this.runAccel * 10) {
            super.setXSpeed(this.runAccel * 10);
        } else if (super.getXSpeed() < -this.runAccel * 10) {
            super.setXSpeed(-this.runAccel * 10);
        }

        // slow the creature down due to friction
        if (super.getXSpeed() > 0.5) {
            sound.walkSound(slowmoTracker);
            super.setXSpeed(super.getXSpeed() - 0.5);
        } else if (super.getXSpeed() < -0.5) {
            sound.walkSound(slowmoTracker);
            super.setXSpeed(super.getXSpeed() + 0.5);
        } else {
            super.setXSpeed(0);
            sound.stopWalkSound();
        }

        // stop the walk sound when the creature is off the ground
        if (jumps < maxJumps) {
            sound.stopWalkSound();
        }

        super.update(entities, bullets, slowmoTracker);

        // add x speed to the x location
        super.setX((super.getX() + super.getXSpeed() * slowmoTracker.getActiveSlowAmount()));
        // check for collisions with other entities. If so, stop the creature from
        // moving any further
        for (int i = 0; i < entities.size(); i++) {
            if (!this.equals(entities.get(i)) && super.rectRectDetect(this, entities.get(i))
                    && entities.get(i).getTouchable() && !entities.get(i).getType().equals("Platform")) {
                super.setX((super.getX() - super.getXSpeed() * slowmoTracker.getActiveSlowAmount()));
                super.setXSpeed(0);
            }
        }

        // add y speed to the y location
        super.setY((super.getY() + super.getYSpeed() * slowmoTracker.getActiveSlowAmount()));
        // check for collision. If so, stop the creature from moving any further
        for (int i = 0; i < entities.size(); i++) {
            if (!this.equals(entities.get(i)) && super.rectRectDetect(this, entities.get(i))
                    && entities.get(i).getTouchable()) {
                // if the entity is a platform, check that the player is above the platform
                if (entities.get(i).getType().equals("Platform")) {
                    if (this.getY() + this.getWidth() < entities.get(i).getY() + 10) {
                        super.setY(entities.get(i).getY() - super.getWidth());
                        super.setYSpeed(0);
                        // if the creature is on top of something, then reset its jumps
                        if (super.getY() < entities.get(i).getY()) {
                            this.jumps = this.maxJumps;
                        }
                    }
                }
                // if the entity is not a platform:
                else {
                    super.setY((super.getY() - super.getYSpeed() * slowmoTracker.getActiveSlowAmount()));
                    super.setYSpeed(0);
                    // if the creature is on top of something, then reset its jumps
                    if (super.getY() < entities.get(i).getY()) {
                        this.jumps = this.maxJumps;
                    }
                }
            }
        }

        // ANIMATION PART
        if (super.getFrames() != null) {

            this.setLastAnimation(super.getRow()); //set last animation to current row #

            if (!canAttack) {
                super.setRow(Const.ATTACK);
            } else if (jumps < maxJumps) {
                super.setRow(Const.JUMP);

            } else if (super.getXSpeed() != 0) {
                super.setRow(Const.MOVE);

            } else if (super.getXSpeed() == 0 && super.getYSpeed() == 0) {
                super.setRow(Const.IDLE);

            }
            //if frames out of bounds or new animation startddds
            if (super.getCol() >= super.getFrames().get(super.getRow()).size()
                    || super.getRow() != this.getLastAnimation()) {
                super.setCol(0);
            }
            if ((System.currentTimeMillis() - super.getAnimationTime())
                    * slowmoTracker.getActiveSlowAmount() > frameUpdate) {
                super.setCol((super.getCol() + 1) % super.getFrames().get(super.getRow()).size());
                super.setAnimationTime(System.currentTimeMillis());
            }
        }

        // if the creature's health is 0 or less, then remove it
        if (this.hp <= 0) {
            // send blood particles flying everywhere
            for (int i = 0; i < 10; i++) {
                entities.add(new Blood((int) this.getX() + this.getLength() / 2,
                        (int) this.getY() + this.getWidth() / 2,
                        randint(-20, 20), randint(-30, 0), Color.RED));
            }
            // play the death sound and remove the creature
            sound.deathSound();
            removeThis(entities);
        }
    }

    // this method will deal damage to the creature
    @Override
    public void takeDamage(double damage) {
        if (!invincible)
            this.hp -= damage;
    }

    // this method will remove this creature from an arraylist of entities
    @Override
    public void removeThis(ArrayList<Entity> entities) {
        sound.stopWalkSound();
        super.removeThis(entities);
    }

    // getters

    public int getLastAnimation() {
        return lastAnimation;
    }

    public double getRunAccel() {
        return this.runAccel;
    }

    public double getJumpSpeed() {
        return this.jumpSpeed;
    }

    public int getJumps() {
        return this.jumps;
    }

    public boolean getCanAttack() {
        return this.canAttack;
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public double getLastAttack() {
        return lastAttack;
    }

    public int getMaxJumps() {
        return this.maxJumps;
    }

    @Override
    public double getHp() {
        return this.hp;
    }

    public double getMaxHp() {
        return this.maxHp;
    }

    public boolean getInvincible() {
        return this.invincible;
    }

    // setters
    public void setMaxHp(double hp) {
        this.maxHp = hp;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }

    public void setLastAnimation(int lastAnimation) {
        this.lastAnimation = lastAnimation;
    }
    public void setRunAccel(double runAccel) {
        this.runAccel = runAccel;
    }

    public void setJumpSpeed(double jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

    public void setJumps(int jumps) {
        this.jumps = jumps;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public void setLastAttack(double lastAttack) {
        this.lastAttack = lastAttack;
    }

    public void setHp(double hp) {
        this.hp = hp;
        if (this.hp > maxHp) {
            this.hp = maxHp;
        }
    }

    public void setMaxJumps(int jumps) {
        this.maxJumps = jumps;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
}
