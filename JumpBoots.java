/**
 * Desc: Jump boots grant the player an extra jump
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */
public class JumpBoots extends Item {
    JumpBoots(int x, int y, int length, int width, int cost, String description) {
        super("JumpBoots", x, y, length, width, cost, description);
    }

    // this runs when the item is activated. Increase max jumps by 1
    @Override
    public void activateItem(Creature user) {
        user.setMaxJumps(user.getMaxJumps() + 1);
    }
}
