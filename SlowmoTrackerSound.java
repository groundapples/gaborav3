/**
 * SlowmoTrackerSound plays the sound of slowing down time
 * 
 * @author Richard, Raymond
 * @version May 2022
 */
public class SlowmoTrackerSound {
    private Sound slowSound = new Sound("audio/slowSound.wav");
    private Sound clockSound = new Sound("audio/clockSound.wav");

    SlowmoTrackerSound() {

    }

    // play the sound of slowing down time
    public void slowSound() {
        if (!slowSound.isRunning()) {
            slowSound.stop(); // stop the sound effect if still running
            slowSound.flush(); // clear the buffer with audio data
            slowSound.setFramePosition(0); // prepare to start from the beginning
            slowSound.start();
        }
        if (!clockSound.isRunning()) {
            clockSound.stop(); // stop the sound effect if still running
            clockSound.flush(); // clear the buffer with audio data
            clockSound.setFramePosition(0); // prepare to start from the beginning
            clockSound.start();
        }
    }

    // stop all of the audio when time resumes
    public void resumeTimeSound() {
        slowSound.stop(); // stop the sound effect if still running
        slowSound.flush(); // clear the buffer with audio data
        slowSound.setFramePosition(0); // prepare to start from the beginning
        clockSound.stop(); // stop the sound effect if still running
        clockSound.flush(); // clear the buffer with audio data
        clockSound.setFramePosition(0); // prepare to start from the beginning
    }

}