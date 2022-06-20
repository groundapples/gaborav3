import java.util.*;
import java.awt.Color;

/**
 * Desc: Boxes are breakable objects in the player's path
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Box extends Wall {
    private int hp;
    private Sound deathSound = new Sound("audio/boxBreak.wav");

    Box(int x, int y, int length, int width, String picName, int hp) {
        super(x, y, length, width, picName);
        super.setGravity(0.2);
        this.hp = hp;
        super.setType("Box");
    }

    // This is run when the object takes damage from bullets
    @Override
    public void takeDamage(double damage) {
        this.hp -= damage;
    }

    // update the object itself (move x, y, collisions, etc)
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        super.update(entities, bullets, slowmoTracker);

        // change its y location by its y speed
        super.setY((super.getY() + super.getYSpeed() * slowmoTracker.getActiveSlowAmount()));
        // check for collisions. If it collides, stop it from moving further
        for (int i = 0; i < entities.size(); i++) {
            if (!this.equals(entities.get(i)) && super.rectRectDetect(this, entities.get(i))
                    && entities.get(i).getTouchable()) {
                super.setY((super.getY() - super.getYSpeed() * slowmoTracker.getActiveSlowAmount()));
                super.setYSpeed(0);
            }
        }

        // check if the object is destroyed (0 or less hp remaining)
        if (this.hp <= 0) {
            // send box particles flying everywhere if it is destroyed
            for (int i = 0; i < 3; i++) {
                entities.add(new Blood((int) this.getX() + this.getLength() / 2,
                        (int) this.getY() + this.getWidth() / 2,
                        randint(-20, 20), randint(-30, 0), new Color(150, 75, 0)));
            }
            // play the death sound (box break)
            deathSound.start();
            // delete the entity after death
            entities.remove(this);
        }
    }
}
