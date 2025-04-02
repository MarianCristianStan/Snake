package utils;

import javax.sound.sampled.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {

	  private static final Map<String, Clip> soundCache = new HashMap<>();
	  
    public static void playSound(String path) {
        try {
            InputStream audioSrc = SoundPlayer.class.getResourceAsStream(path);
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    
    public static Clip getClip(String path) {
        if (soundCache.containsKey(path)) {
            return soundCache.get(path);
        }

        try {
            InputStream audioSrc = SoundPlayer.class.getResourceAsStream(path);
            if (audioSrc == null) {
                System.err.println("Sound not found: " + path);
                return null;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundCache.put(path, clip);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
