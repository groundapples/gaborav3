import java.util.*;
import java.awt.*;

/**
 * Desc: This is the blood particle. It is a red rectangle that shoots out of
 * creatures when they die
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Blood extends Particle {
    private Color color;

    Blood(int x, int y, double xSpeed, double ySpeed, Color color) {
        super(x, y, 10, 10, "");
        super.setType("Blood");
        super.setXSpeed(xSpeed);
        super.setYSpeed(ySpeed);
        super.setTeam(-1);
        super.setDuration(1500);
        super.setGravity(0.2);
        super.setTouchable(false);
        this.color = color;
    }

    // draw blood cubes on the screen
    @Override
    public void draw(Graphics g, int xRange, int yRange, SlowmoTracker slowmoTracker) {
        g.setColor(color);
        g.fillRect((int) getX() - xRange, (int) getY() - yRange, getLength(), getWidth());
    }

    // Run this method to update the blood particle (Move it's x and y, as well as
    // collisions)
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        super.update(entities, bullets, slowmoTracker);

        // move the x location by the x speed and slow it by how slow time is
        super.setX((super.getX() + super.getXSpeed() * slowmoTracker.getActiveSlowAmount()));
        // if the item bumps into anything, stop it from moving any further
        for (int i = 0; i < entities.size(); i++) {
            if (!this.equals(entities.get(i)) && super.rectRectDetect(this, entities.get(i))
                    && entities.get(i).getTouchable()) {
                super.setX((super.getX() - super.getXSpeed() *
                        slowmoTracker.getActiveSlowAmount()));
                super.setXSpeed(0);
            }
        }
        // move the y location by the y speed
        super.setY((super.getY() + super.getYSpeed() * slowmoTracker.getActiveSlowAmount()));
        // check for collision and if so stop it from moving.
        for (int i = 0; i < entities.size(); i++) {
            if (!this.equals(entities.get(i)) && super.rectRectDetect(this, entities.get(i))
                    && entities.get(i).getTouchable()) {
                // entities.remove(this);
                super.setY((super.getY() - super.getYSpeed() *
                        slowmoTracker.getActiveSlowAmount()));
                super.setYSpeed(0);
            }
        }
    }
}
