/**
 * Platforms are walls that can be jumped through from below
 * 
 * @author Richard, Raymond
 * @version May 2022
 */

public class Platform extends Wall {
    Platform(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setType("Platform");
    }
}