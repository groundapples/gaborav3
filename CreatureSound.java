/**
 * Desc: Creatures have a walk sound and a death sound, both of which are
 * contained in this object
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class CreatureSound {
    Sound walkSound = new Sound("audio/walk.wav");
    Sound deathSound = new Sound("audio/deathSound.wav");

    CreatureSound() {

    }

    // this will play the walk sound only when time is not slowed
    public void walkSound(SlowmoTracker slowmoTracker) {
        if (slowmoTracker.getActiveSlowAmount() == 1) {
            if (!walkSound.isRunning()) {
                walkSound.stop(); // stop the sound effect if still running
                walkSound.flush(); // clear the buffer with audio data
                walkSound.setFramePosition(0); // prepare to start from the beginning
                walkSound.start();
            }
        }
    }

    // this will stop the walk sound
    public void stopWalkSound() {
        walkSound.stop(); // stop the sound effect if still running
        walkSound.flush(); // clear the buffer with audio data
        walkSound.setFramePosition(0); // prepare to start from the beginning
    }

    // this will play the sound of a creature dying
    public void deathSound() {
        deathSound.start();
    }
}
