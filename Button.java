import java.util.*;

/**
 * Desc: the button is used only at the end of each level. The player will
 * interact with it to trigger the next level
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Button extends Wall {
    private Sound leverSound = new Sound("audio/leverSound.wav");

    Button(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setType("Button");
        super.setTouchable(false);
    }

    // This method will dictate what happens when another entity interacts with this
    // one.
    // In this case, it will reset the entire map and generate a new one
    @Override
    public void interact(Entity interactor, Map map, ArrayList<Entity> entities, Music music) {
        // if the map is ready to be loaded, then proceed with the loading
        if (map.getMapLoading()) {
            // remove all of the entities
            for (int i = entities.size() - 1; i >= 0; i--) {
                entities.get(i).removeThis(entities);
            }
            // add the player as the first entity
            entities.add(interactor);
            // set player position
            interactor.setX(100);
            interactor.setY(100);
            // clear all of the rooms in the map
            map.emptyRooms();
            // generate a new level
            map.recreate(0, 0, 10, entities);
            // start up a random background song
            music.loadRandomSong();
            music.start();
            // set that the map is done loading
            map.setMapLoading(false);
        } else {
            // set that the map is ready to begin loading
            map.setMapLoading(true);
            // play the sound of the player interacting with the button
            leverSound.start();
            music.stop();
        }
    }

}
