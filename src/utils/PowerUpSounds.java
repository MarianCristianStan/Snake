package utils;

public class PowerUpSounds {
    private static final String COLLECT_PATH = "/assets/sounds/pick_power.wav";
    private static final String ACTIVATE_PATH = "/assets/sounds/activate_power.wav";

    public static void playCollect() {
        SoundPlayer.playSound(COLLECT_PATH);
    }

    public static void playActivate() {
        SoundPlayer.playSound(ACTIVATE_PATH);
    }
}

