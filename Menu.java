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
public class Menu {
    public BufferedImage background;
    public Rectangle playButton = new Rectangle(200, 150, 100, 50);
    public Rectangle helpButton = new Rectangle(200, 250, 100, 50);
    public Rectangle quitButton = new Rectangle(200, 350, 100, 50);
    Menu(){

    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        try {
            background = ImageIO.read(new File("images/screen.jpg"));
        } catch(IOException e){
            e.printStackTrace();
        }
        g.drawImage(background, 0, 0, null);
        Font font1 = new Font("Arial", Font.BOLD, 50);
        g.setFont(font1);
        g.setColor(Color.BLACK);
        g.drawString("GABORA", 200, 100);

        Font font2 = new Font("Arial", Font.BOLD, 30);
        g.setFont(font2);
        g.drawString("Play", playButton.x + 21, playButton.y + 35);
        g.drawString("Help", helpButton.x + 20, helpButton.y + 35);
        g.drawString("Quit", quitButton.x + 21, quitButton.y + 35);


        g2d.draw(playButton);
        g2d.draw(helpButton);
        g2d.draw(quitButton);
    }
}
