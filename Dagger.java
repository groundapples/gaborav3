/**
 * Desc: The dagger is an
 * item that will allow the player to attack faster
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Dagger extends Item {
    Dagger(int x, int y, int length, int width, int cost, String description) {
        super("Dagger", x, y, length, width, cost, description);
    }

    // this method will run when the item is purchased by the player
    @Override
    public void activateItem(Creature user) {
        user.setAttackCooldown(user.getAttackCooldown() - 5);
    }
}
