import java.util.*;

/**
 * Desc: Dash is a bullet that the player leaves behind when they dash to deal
 * damage to anything in the player's path
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Dash extends Bullet {
    Dash(double x, double y, double aimX, double aimY, int r, double speed, int team,
            int damage, int distance, boolean isRemovedOnHit, String picName, Entity shooter) {
        super(x, y, aimX, aimY, r, speed, team, damage, distance, isRemovedOnHit, picName, shooter);
        super.setSound("audio/dashSound.wav");
        super.setDeflectable(false);
    }

    // Remove all other bullets touching this bullet
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            if (super.circCircDetect(bullets.get(i), this) && this != bullets.get(i)
                    && super.getTeam() != bullets.get(i).getTeam()) {
                bullets.remove(i);
            }
        }
        super.update(entities, bullets, slowmoTracker);
    }
}