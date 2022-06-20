import java.util.*;
import java.awt.*;

/**
 * Desc: the Camera object will keep track of where the player is and center the
 * game on them
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Camera {
    private int xRange; // the number needed to be subtracted from any entity's x to be in the correct
                        // position on the screen
    private int yRange; // the number needed to be subtracted from any entity's y to be in the correct
                        // position on the screen
    private int focusIndex; // the player's index in ArrayList entities
    private Font font = new Font("TimesRoman", Font.PLAIN, 100);
    public Rectangle quitButton = new Rectangle(Const.WIDTH / 2 - 13, 250, 100, 50);

    Camera(int focusIndex) {
        xRange = 0;
        yRange = 0;
        this.focusIndex = focusIndex;
    }

    // this method will update the camera so that it is keeping track of the player
    public void update(ArrayList<Entity> entities) {
        // if the focus index is still on the player (if the player is still alive),
        // then update xRange and yRange
        if (entities.get(focusIndex).getType().equals("Player")) {
            xRange = (int) (entities.get(focusIndex).getX() + entities.get(focusIndex).getLength() / 2
                    - Const.WIDTH / 2);
            yRange = (int) (entities.get(focusIndex).getY() + entities.get(focusIndex).getWidth() / 2
                    - Const.HEIGHT / 2);
        }
    }

    // draw the game over screen if the player is dead
    public void draw(Graphics g, ArrayList<Entity> entities) {
        if (!entities.get(focusIndex).getType().equals("Player")) {
            Graphics2D g2d = (Graphics2D) g;
            g.setFont(font);
            g.setColor(Color.red);
            g.drawString("GAME OVER", Const.WIDTH / 2 - 300, Const.HEIGHT / 2 - 100);


        }
    }

    // getters
    public int getXRange() {
        return xRange;
    }

    public int getYRange() {
        return yRange;
    }
}
