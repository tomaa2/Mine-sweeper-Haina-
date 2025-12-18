package Model;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private static boolean muted = false;

    private static AudioClip CLICK = null;
    private static AudioClip MINE = null;
    private static AudioClip GAME_OVER = null;

    static {
        // Load sounds safely at startup
        CLICK = load("/sounds/click.mp3");
      //  MINE = load("/sounds/mine.mp3");
       // GAME_OVER = load("/sounds/gameover.mp3");
    }

    private static AudioClip load(String path) {
        try {
            var resource = SoundManager.class.getResource(path);
            if (resource == null) {
                System.out.println("Sound not found: " + path);
                return null;
            }
            return new AudioClip(resource.toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not load sound: " + path + " - " + e.getMessage());
            return null;
        }
    }

    public static void setMuted(boolean value) { 
        muted = value; 
    }

    public static boolean isMuted() { 
        return muted; 
    }

    public static void playClick() {
        if (!muted && CLICK != null) CLICK.play();
    }

    public static void playMine() {
        if (!muted && MINE != null) MINE.play();
    }

    public static void playGameOver() {
        if (!muted && GAME_OVER != null) GAME_OVER.play();
    }
}