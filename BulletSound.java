/**
 * Desc: Bullet sound will control what sound bullets make when they are shot
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */
public class BulletSound {
    private Sound sound;
    private boolean played = false; // Has the bullet sound been played yet?

    BulletSound(String bulletSound) {
        sound = new Sound(bulletSound);
    }

    // this method will play the sound of the bullet
    public void attackSound() {
        sound.start();
        played = true;
    }

    // setters
    public void setShotSound(String soundName) {
        this.sound = new Sound(soundName);
    }

    // getters
    public boolean getPlayed() {
        return played;
    }
}
