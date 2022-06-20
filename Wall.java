import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// possible exceptions
import java.io.IOException;

/**
 * Wall is a solid entity that blocks bullets and other entities
 * 
 * @author Richard, Raymond
 * @version May 2022
 */

public class Wall extends Entity {
    private BufferedImage image;

    Wall(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setType("Wall");
        super.setTeam(-1);
    }

    // draw the wall if it is in range
    @Override
    public void draw(Graphics g, int xRange, int yRange, SlowmoTracker slowmoTracker) {
        if (super.checkInRange(xRange, yRange)) {
            // walls are static, only have 1 image
            for (int i = 0; i < super.getLength(); i += 50) {
                g.drawImage(image, (int) super.getX() - xRange + i, (int) super.getY() - yRange,
                        null);
            }
        }

    }

    // load in the single image for the wall (walls only have 1 image, they are
    // static)
    @Override
    public void loadImages() {
        if (!super.getPicName().equals("")) {
            try {
                image = ImageIO.read(new File("images/" + super.getPicName() + ".png"));
            } catch (IOException ex) {
                System.out.println(ex);
                System.out.println("failed to load wall");
            }
        }
    }

    // getters
    public BufferedImage getImage() {
        return image;
    }
}
