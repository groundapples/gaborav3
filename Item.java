import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// possible exceptions
import java.io.IOException;

/**
 * Desc: Items are stored within stores and can be purchased and activated by
 * the player
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Item {
    private String name;
    private BufferedImage image, coin;
    private Font font = new Font("TimesRoman", Font.PLAIN, 20);
    private int x, y, length, width, cost;
    private String description;

    Item(String name, int x, int y, int length, int width, int cost, String description) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.length = length;
        this.width = width;
        this.cost = cost;
        this.description = description;
        loadImages();
    }

    // draw the item's image and it's details
    public void draw(Graphics g, int mouseX, int mouseY) {
        g.drawImage(image, x, y, null);
        // display the cost of the item
        g.setColor(Color.black);
        g.setFont(font);
        g.drawString(Integer.toString(cost), x + 10, y - 30);
        // coin icon
        g.drawImage(coin, x + 25, y - 45, null);

        // when the mouse is hovering over the item, display basic info
        if (this.contains(mouseX, mouseY)) {
            g.setColor(Color.black);
            g.fillRect(mouseX - 202, mouseY - 22, 204, 44);
            g.setColor(new Color(105, 165, 181));
            g.fillRect(mouseX - 200, mouseY - 20, 200, 40);
            g.setColor(Color.black);
            g.setFont(font);
            g.drawString(description, mouseX - 180, mouseY + 10);
        }
    }

    // checks if the mouse location is within the item itself
    public boolean contains(int x, int y) {
        if (x > this.x && x < this.x + this.length &&
                y > this.y && y < this.y + this.width) {
            return true;
        }
        return false;
    }

    // load the coin image and the image of the item
    public void loadImages() {
        if (!name.equals("")) {
            try {
                image = ImageIO.read(new File("images/items/" + name + ".png"));
                coin = ImageIO.read(new File("images/coin.png"));
            } catch (IOException ex) {
                System.out.println(ex);
                System.out.println("failed to load image");
            }
        }
    }

    // run this when the item is activated. By default, does nothing
    public void activateItem(Creature user) {

    }

    // getters
    public int getCost() {
        return cost;
    }
}
