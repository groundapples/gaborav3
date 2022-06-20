import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// possible exceptions
import java.io.IOException;

/**
 * Desc: this will 
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Background {
    int x, y;
    BufferedImage backgroundImage;

    Background(int x, int y) {
        this.x = x;
        this.y = y;
        String[] possibleBackgrounds = { "background01" };
        try {
            backgroundImage = ImageIO.read(new File("images/backgrounds/" +
                    possibleBackgrounds[randint(0, possibleBackgrounds.length - 1)] + ".png"));
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("failed to load background");
        }
    }

    public void draw(Graphics g, int xRange, int yRange) {
        g.drawImage(backgroundImage, x, y, null);
    }

    public void update(int xRange, int yRange) {
        if (x + Const.WIDTH < xRange) {
            x += Const.WIDTH;
        } else if (x - Const.WIDTH > xRange) {
            x -= Const.WIDTH;
        }

        if (y + Const.WIDTH < yRange) {
            y += Const.WIDTH;
        } else if (y - Const.WIDTH > yRange) {
            y -= Const.WIDTH;
        }
    }

    public static int randint(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
