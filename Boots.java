/**
 * Desc: Boots will speed the player's base speed up
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Boots extends Item {
    Boots(int x, int y, int length, int width, int cost, String description) {
        super("Boots", x, y, length, width, cost, description);
    }

    // this method will run when the item is purchased
    @Override
    public void activateItem(Creature user) {
        user.setRunAccel(user.getRunAccel() + 0.1); // increase the player's acceleration
    }
}
