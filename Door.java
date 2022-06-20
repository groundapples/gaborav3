import java.util.*;

/**
 * Desc: Doors are moveable walls that will move when creatures interact with
 * them
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Door extends Wall {
    private Sound doorOpenSound = new Sound("audio/doorOpenSound.wav");

    Door(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setType("Door");
    }

    // This method causes the door to slide upwards
    @Override
    public void interact(Entity interactor, Map map, ArrayList<Entity> entities, Music music) {
        this.setYSpeed(-10);
        doorOpenSound.start();
    }

    // this method will cause the door to slide upwards. Method overloading
    @Override
    public void interact(Entity interactor, ArrayList<Entity> entities) {
        this.setYSpeed(-10);
        doorOpenSound.start();
    }

    // this method will update the location of the door in accordance to its speed
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        this.setX(this.getX() + this.getXSpeed() * slowmoTracker.getActiveSlowAmount());
        this.setY(this.getY() + this.getYSpeed() * slowmoTracker.getActiveSlowAmount());
        super.update(entities, bullets, slowmoTracker);
    }

}
