import java.util.*;

/**
 * The rocket guard is a slow moving enemy that shoots rockets at you. It has a
 * long attack cooldown
 * 
 * @author Richard, Raymond
 * @version May 2022
 */
public class RocketGuard extends Enemy {
    RocketGuard(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setGravity(0.3);
        super.setDetectRange(500);
        super.setEngageRange(400);
        super.setRunAccel(0.45);
        super.setAttackCooldown(80);
        super.setJumpSpeed(4);
        super.setTeam(1);
        super.setMaxHp(30);
    }

    // as an attack, it wills hoot a rocket at you
    public void attack(ArrayList<Entity> entities, ArrayList<Bullet> bullets) {
        bullets.add(new Rocket(super.getX() + super.getLength() / 2, super.getY() + super.getWidth() / 2,
                super.getDestinationX() + randint(-10, 10), super.getDestinationY() + randint(-10, 10),
                20, 10, super.getTeam(), 10, 5000, true,
                "rocket", this));
    }

    // search for the player and update
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        super.updateDestination(entities);
        super.update(entities, bullets, slowmoTracker);
        super.search(entities, bullets, slowmoTracker);
    }
}
