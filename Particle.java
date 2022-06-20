import java.util.*;

/**
 * Particles are entities that are only on the screen for a select amount of
 * time
 * 
 * @author Richard, Raymond
 * @version May 2022
 */

public class Particle extends Entity {
    private long creationTime; // when was the particle created
    private double duration; // how long does it last

    Particle(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setType("Particle");
        super.setTeam(-1);
        this.creationTime = System.currentTimeMillis();
        this.duration = 0;
    }

    // update the particle itself (move it, etc)
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        this.setX(this.getX() + this.getXSpeed() * slowmoTracker.getActiveSlowAmount());
        this.setY(this.getY() + this.getYSpeed() * slowmoTracker.getActiveSlowAmount());
        super.update(entities, bullets, slowmoTracker);
        // if the particle's duration runs out, remove it
        if ((System.currentTimeMillis() - creationTime) * slowmoTracker.getActiveSlowAmount() > duration) {
            entities.remove(this);
        }
    }

    // setters
    public void setDuration(double duration) {
        this.duration = duration;
    }
}
