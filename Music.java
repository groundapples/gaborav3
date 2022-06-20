import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
// possible exceptions
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

/**
 * Music will load a random soundtrack and play it
 * 
 * @author Richard, Raymond
 * @version May 2022
 */
public class Music {
    Clip music;

    // ------------------------------------------------------------------------------
    Music(String musicName) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(musicName));
            this.music = AudioSystem.getClip();
            this.music.open(audioStream);
        } catch (IOException ex) {
            System.out.println("File not found!");
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("Unsupported file!");
        } catch (LineUnavailableException ex) {
            System.out.println("Audio feed already in use!");
        }
    }

    Music() {
        loadRandomSong();
    }

    // ------------------------------------------------------------------------------
    // start the song
    public void start() {
        this.music.start();
    }

    // loop the song
    public void loop() {
        this.music.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // loop the song a specific number of times
    public void loop(int count) {
        this.music.loop(count); // loop the playback certain number of times
    }

    // stop the song
    public void stop() {
        this.music.stop();
        this.music.flush();
        this.music.setFramePosition(0);
    }

    // load a new song
    public void loadNewSong(String musicName) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(musicName));
            this.music = AudioSystem.getClip();
            this.music.open(audioStream);
        } catch (IOException ex) {
            System.out.println("File not found!");
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("Unsupported file!");
        } catch (LineUnavailableException ex) {
            System.out.println("Audio feed already in use!");
        }
    }

    // load a random song
    public void loadRandomSong() {
        // list of all soundtracks in the game
        String[] soundtracks = {"MeatGrinder.wav", "Overdose.wav", "BreathOfASerpent.wav"};
        // select a random song and play it
        String randomSong = soundtracks[randint(0, soundtracks.length - 1)];
        loadNewSong("audio/soundtracks/" + randomSong);
    }

    // give a random integer
    public int randint(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}