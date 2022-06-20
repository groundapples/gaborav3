import java.awt.Graphics;
import java.io.File;
import java.util.*;

/**
 * Shop has items on sale that the player can purchase
 * 
 * @author Richard, Raymond
 * @version May 2022
 */
public class Shop extends Wall {
    private ArrayList<Item> items;

    Shop(int x, int y, int length, int width, String picName, int numberOfItemsOnSale) {
        super(x, y, length, width, picName);
        super.setTouchable(false);
        items = new ArrayList<Item>();
        loadShopItems(numberOfItemsOnSale);
        super.setType("Shop");
    }

    // load in all of the items on sale
    public void loadShopItems(int numberOfItemsOnSale) {
        // go into items.txt and find the number of unique items
        File shopFile = new File("");
        Scanner shopScanner = new Scanner("");
        try {
            shopFile = new File("items.txt");
            shopScanner = new Scanner(shopFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        int numberOfCustomItems = 0;
        shopScanner = loadItem("items.txt", 0);
        while (shopScanner.hasNext()) {
            if (shopScanner.nextLine().equals("---")) {
                numberOfCustomItems++;
            }
        }

        // load in the number of items the shop is selling
        for (int i = 0; i < numberOfItemsOnSale; i++) {
            // get a random item
            int randomItem = randint(0, numberOfCustomItems - 1);
            // load the item in
            shopScanner = loadItem("items.txt", randomItem);
            // the x and y location of the item in the menu
            int x = 250 + i * 150;
            int y = 460;
            String itemName = shopScanner.next();
            int cost = shopScanner.nextInt();
            shopScanner.nextLine(); // get rid of the space between description and cost.
            String description = shopScanner.nextLine();
            // add in the item itself
            if (itemName.equals("Boots")) {
                items.add(new Boots(x, y, 100, 100, cost, description));
            } else if (itemName.equals("HealthPotion")) {
                items.add(new HealthPotion(x, y, 100, 100, cost, description));
            } else if (itemName.equals("Dagger")) {
                items.add(new Dagger(x, y, 100, 100, cost, description));
            } else if (itemName.equals("Vest")) {
                items.add(new Vest(x, y, 100, 100, cost, description));
            } else if (itemName.equals("JumpBoots")) {
                items.add(new JumpBoots(x, y, 100, 100, cost, description));
            }
        }
    }

    // draw the shop (sans undertale)
    @Override
    public void draw(Graphics g, int xRange, int yRange, SlowmoTracker slowmoTracker) {
        if (super.checkInRange(xRange, yRange)) {
            // walls are static, only have 1 image
            g.drawImage(getImage(), (int) super.getX() - xRange, (int) super.getY() - yRange,
                    null);
        }

    }

    // open the shop when interacted with
    @Override
    public void interact(Entity interactor, Map map, ArrayList<Entity> entities, Music music) {
        if (interactor.getInteractingWith() == null) {
            interactor.setInteractingWith(this);
        } else {
            interactor.setInteractingWith(null);
        }
    }

    // update the shop itself
    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        // when the player gets too far away, close the shop
        if (distance(getX() + getLength() / 2, getY() + getWidth() / 2,
                entities.get(0).getX() + entities.get(0).getLength() / 2,
                entities.get(0).getY() + entities.get(0).getWidth() / 2) > 200) {
            entities.get(0).setInteractingWith(null);
        }
        super.update(entities, bullets, slowmoTracker);
    }

    // load in the item number in from the item file
    public Scanner loadItem(String fileName, int itemNumber) {
        File shopFile = new File("");
        Scanner shopScanner = new Scanner("");
        try {
            shopFile = new File(fileName);
            shopScanner = new Scanner(shopFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }
        int itemsPassed = 0;
        while (itemsPassed < itemNumber) {
            String text = shopScanner.nextLine();
            if (text.equals("---")) {
                itemsPassed++;
            }
        }
        return shopScanner;
    }

    // getters
    @Override
    public ArrayList<Item> getItems() {
        return this.items;
    }
}
