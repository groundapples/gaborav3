import java.util.*;

/**
 * Rockets are bullets that explode on impact
 * 
 * @author Richard, Raymond
 * @version May 2022
 */

public class Rocket extends Bullet {
    Rocket(double x, double y, double aimX, double aimY, int r, double speed, int team,
            int damage, int distance, boolean isRemovedOnHit, String picName, Entity shooter) {
        super(x, y, aimX, aimY, r, speed, team, damage, distance, isRemovedOnHit, picName, shooter);
        super.setSound("audio/rocketshot.wav");
    }

    // when removing the rocket, leave a secondary bullet in place that does a lot
    // of damage
    @Override
    public void remove(ArrayList<Bullet> bullets) {
        bullets.add(new Bullet(getX(), getY(), getAimX(), getAimY(), 100, 0.1, this.getTeam(),
                20, 1, false, "explosion", getShooter()));
        bullets.get(bullets.size() - 1).setSound("audio/explosion.wav");
        bullets.remove(this);
    }

}
