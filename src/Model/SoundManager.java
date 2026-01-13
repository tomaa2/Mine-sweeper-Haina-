package Model;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
	//settings
    private static boolean muted = false;
    private static double sfxVolume = 0.7;      // [0.0-1.0]
    private static double musicVolume = 0.4;    // [0.0-1.0]
    
    //background music
    private static MediaPlayer BACKGROUNDMUSIC = null;
    //ui sounds
    private static AudioClip CLICK = null;
    private static AudioClip HOVER = null;
    //cells sounds effects
    private static AudioClip MINE_EXPLODE = null;
    private static AudioClip REVEAL = null;
    private static AudioClip REVEAL_EMPTY = null;
    private static AudioClip FLAG_PLACE = null;
    private static AudioClip FLAG_REMOVE = null;
    //question cells effects
    private static AudioClip QUESTION_OPEN = null;
    private static AudioClip QUESTION_CORRECT = null;
    private static AudioClip QUESTION_WRONG = null;
    //surprise cells effects
    private static AudioClip SURPRISE_OPEN = null;
    private static AudioClip SURPRISE_GOOD = null;
    private static AudioClip SURPRISE_BAD = null;
    //sound effects for life changes
	private static AudioClip LIFE_LOST = null;
	private static AudioClip LIFE_GAINED = null;
	//game state sounds
    private static AudioClip GAME_OVER = null;
    private static AudioClip GAME_WIN = null;
    private static AudioClip TURN_SWITCH = null;
    //score change sounds
    private static AudioClip SCORE_UP = null;
    private static AudioClip SCORE_DOWN = null;
    
    static {
        // Load all sounds safely at startup
        loadAllSounds();

    }

    private static AudioClip loadClip(String path) {
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
    //load all sound effects
    private static void loadAllSounds() {
        //UI sounds
        CLICK = loadClip("/sounds/click.mp3");
        HOVER = loadClip("/sounds/hover.mp3");
        //cell sounds
        MINE_EXPLODE = loadClip("/sounds/explosion.mp3");
        REVEAL_EMPTY = loadClip("/sounds/reveal_empty.mp3");
        FLAG_PLACE = loadClip("/sounds/flag_place.mp3");
        FLAG_REMOVE = loadClip("/sounds/flag_remove.mp3");
        REVEAL = loadClip("/sounds/reveal.mp3");
        //question sounds
        QUESTION_OPEN = loadClip("/sounds/question_open.mp3");
        QUESTION_CORRECT = loadClip("/sounds/correct.mp3");
        QUESTION_WRONG = loadClip("/sounds/wrong.mp3");
        //surprise sounds
        SURPRISE_OPEN = loadClip("/sounds/surprise_open.mp3");
        SURPRISE_GOOD = loadClip("/sounds/surprise_good.mp3");
        SURPRISE_BAD = loadClip("/sounds/surprise_bad.mp3");
        //life sounds
        LIFE_LOST = loadClip("/sounds/life_lose.mp3");
        LIFE_GAINED = loadClip("/sounds/life_gain.mp3");
        //game state sounds
        GAME_OVER = loadClip("/sounds/lose.mp3");
        GAME_WIN = loadClip("/sounds/win.mp3");
        TURN_SWITCH = loadClip("/sounds/turn_switch.mp3");
        //score sounds
        SCORE_UP = loadClip("/sounds/score_up.mp3");
        SCORE_DOWN = loadClip("/sounds/score_down.mp3");
        
        System.out.println("SoundManager initialized.");
    }
    //play a sound clip
    private static void play(AudioClip clip) {
        if (!muted && clip != null) {
            clip.play(sfxVolume);
        }
    }
    //play a sound clip with volume multiplier
    private static void play(AudioClip clip, double volumeMultiplier) {
        if (!muted && clip != null) {
            clip.play(sfxVolume * volumeMultiplier);
        }
    }
    
    //////settings
    public static void setMuted(boolean value) { 
        muted = value;
        if (BACKGROUNDMUSIC != null) {
            BACKGROUNDMUSIC.setMute(value);
        }
    }
    public static boolean isMuted() { 
        return muted; 
    }
    public static void toggleMute() {
        setMuted(!muted);
    }
    public static void setSfxVolume(double volume) {
        sfxVolume = Math.max(0.0, Math.min(1.0, volume));
    }
    public static double getSfxVolume() {
        return sfxVolume;
    }
    public static void setMusicVolume(double volume) {
        musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (BACKGROUNDMUSIC != null) {
            BACKGROUNDMUSIC.setVolume(musicVolume);
        }
    }
    public static double getMusicVolume() {
        return musicVolume;
    }
    //////background settings

    //play background music on loop
    public static void playBackgroundMusic(String path) {
        stopBackgroundMusic();
        try {
            var resource = SoundManager.class.getResource(path);
            if (resource == null) {
                System.out.println("Music not found: " + path);
                return;
            }
            Media media = new Media(resource.toExternalForm());
            BACKGROUNDMUSIC = new MediaPlayer(media);
            BACKGROUNDMUSIC.setCycleCount(MediaPlayer.INDEFINITE);
            BACKGROUNDMUSIC.setVolume(musicVolume);
            BACKGROUNDMUSIC.setMute(muted);
            BACKGROUNDMUSIC.play();
        } catch (Exception e) {
            System.out.println("Could not play music: " + path + " - " + e.getMessage());
        }
    }
    
//stop background music
    public static void stopBackgroundMusic() {
        if (BACKGROUNDMUSIC != null) {
            BACKGROUNDMUSIC.stop();
            BACKGROUNDMUSIC.dispose();
            BACKGROUNDMUSIC = null;
        }
    }
    
///pause background music
    public static void pauseBackgroundMusic() {
        if (BACKGROUNDMUSIC != null) {
            BACKGROUNDMUSIC.pause();
        }
    }
    
///resume background music
    public static void resumeBackgroundMusic() {
        if (BACKGROUNDMUSIC != null) {
            BACKGROUNDMUSIC.play();
        }
    }

    //////UI sounds
    public static void playClick() {
        play(CLICK);
    }
    
    public static void playHover() {
        play(HOVER, 0.3); //keep it low volume
    }

    //////cell sounds
    public static void playReveal() {
        play(REVEAL);
    }
    
    public static void playRevealEmpty() {
        //cascade reveal sound
        play(REVEAL_EMPTY != null ? REVEAL_EMPTY : REVEAL);
    }
    
    public static void playFlagPlace() {
        play(FLAG_PLACE);
    }
    
    public static void playFlagRemove() {
        play(FLAG_REMOVE);
    }
    public static void playMineExplode() {
		play(MINE_EXPLODE);
	}
    //////question sounds
	public static void playQuestionOpen() {
		play(QUESTION_OPEN);
	}
	
	public static void playQuestionCorrect() {
		play(QUESTION_CORRECT);
	}
	
	public static void playQuestionWrong() {
		play(QUESTION_WRONG);
	}
	
	//////surprise sounds
	public static void playSurpriseOpen() {
		play(SURPRISE_OPEN);
	}
	
	public static void playSurpriseGood() {
		play(SURPRISE_GOOD);
	}
	
	public static void playSurpriseBad() {
		play(SURPRISE_BAD);
	}
	
	//////life sounds
	public static void playLifeLost() {
		play(LIFE_LOST);
	}
	public static void playLifeGained() {
		play(LIFE_GAINED);
	}
	//////game state sounds
	public static void playGameOver() {
		play(GAME_OVER);
	}
	public static void playGameWin() {
		play(GAME_WIN);
	}
	public static void playTurnSwitch() {
		play(TURN_SWITCH, 0.5); //should be low volume so its not annoying     
	}
	//////score sounds
    public static void playScoreUp() {
        play(SCORE_UP, 0.4);
    }
    
    public static void playScoreDown() {
        play(SCORE_DOWN, 0.4);
    }
	
    ////helper methods for playing sounds based on game events	
    ///play appropriate sound for cell reveal based on cell type
    public static void playCellReveal(CellType type) {
        switch (type) {
            case MINE -> playMineExplode();
            case EMPTY -> playRevealEmpty();
            case NUMBER -> playReveal();
            case QUESTION -> playReveal();
            case SURPRISE -> playReveal();
        }
    }
    ///plays appropriate sound for surprise cell result
    public static void playSurpriseResult(boolean isGood) {
        if (isGood) {
            playSurpriseGood();
        } else {
            playSurpriseBad();
        }
    }
    ///plays appropriate sound for question answer result
    public static void playAnswerResult(boolean isCorrect) {
        if (isCorrect) {
            playQuestionCorrect();
        } else {
            playQuestionWrong();
        }
    }
    ///plays appropriate sound for game end based on win/loss
    public static void playGameEnd(boolean won) {
        if (won) {
            playGameWin();
        } else {
            playGameOver();
        }
    }
}