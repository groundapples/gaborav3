public class HealthPotion extends Item {
    HealthPotion(int x, int y, int length, int width, int cost, String description) {
        super("HealthPotion", x, y, length, width, cost, description);
    }

    @Override
    public void activateItem(Creature user) {
        user.setHp(user.getHp() + 40);
    }
}
