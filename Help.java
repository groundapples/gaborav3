import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;

/**
 * Desc: Displays the menu
 *
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */
public class Help {
    public BufferedImage background;
    public Rectangle playButton = new Rectangle(200, 400, 100, 50);

    Help(){

    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        try {
            background = ImageIO.read(new File("images/screen.jpg"));
        } catch(IOException e){
            e.printStackTrace();
        }
        g.drawImage(background, 0, 0, null);
        Font font1 = new Font("Arial", Font.BOLD, 15);
        Font font2 = new Font("Arial", Font.BOLD, 30);
        g.setFont(font2);
        g.setColor(Color.BLACK);
        g.drawString("CONTROLS", 200, 100);
        g.drawString("Play", playButton.x + 21, playButton.y + 35);

        g.setFont(font1);
        g.drawString("WASD - Move", 200, 150);
        g.drawString("LMB - Slash/Deflect", 200, 175);
        g.drawString("E - Interact", 200, 200);
        g.drawString("Shift - Toggle Time Slow", 200, 225);
        g.drawString("Space - Dash-Kill", 200, 250);


        g2d.draw(playButton);

    }
}

