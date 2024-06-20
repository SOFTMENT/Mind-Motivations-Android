package in.softment.mindmotivation.Util;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import in.softment.mindmotivation.R;

public class MyAudioPlayer {

    private static final MyAudioPlayer sharedInstance = new MyAudioPlayer();
    private MediaPlayer playerForBackground;
    private MediaPlayer playerForBreathe;
    private float volume = 0.9f;

    // Private constructor to enforce Singleton pattern.
    private MyAudioPlayer() { }

    public static MyAudioPlayer getInstance() {
        return sharedInstance;
    }

    public void playBackground(Context context) {
        stopBackground(); // Stop any currently playing background music first

        Uri backgroundMusicUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.myappbackgroundmusic);
        playerForBackground = MediaPlayer.create(context.getApplicationContext(), backgroundMusicUri);

        if (playerForBackground != null) {
            playerForBackground.setLooping(true);
            playerForBackground.start();
        }
    }

    public void stopBackground() {
        if (playerForBackground != null) {
            playerForBackground.stop();
            playerForBackground.release();
            playerForBackground = null;
        }
    }

    public void changeBackgroundVolume(float volume) {
        if (playerForBackground != null) {
            playerForBackground.setVolume(volume, volume);
        }
    }

    public void playBreathe(Context context) {
        stopBreathe(); // Stop any currently playing breathe music first

        Uri breatheMusicUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.breathe);
        playerForBreathe = MediaPlayer.create(context.getApplicationContext(), breatheMusicUri);

        if (playerForBreathe != null) {
            playerForBreathe.setLooping(true);
            playerForBreathe.start();
        }
    }

    public void stopBreathe() {
        if (playerForBreathe != null) {
            playerForBreathe.stop();
            playerForBreathe.release();
            playerForBreathe = null;
        }
    }

    public float getBreatheVolume() {
        // This functionality is not available in MediaPlayer
        return volume;
    }

    public void changeBreatheVolume(float volume) {
        if (playerForBreathe != null) {
            this.volume = volume;
            playerForBreathe.setVolume(volume, volume);
        }
    }

    public boolean isBreathePlaying() {
        return playerForBreathe != null && playerForBreathe.isPlaying();
    }

    public int getCurrentTimeForBreathe() {
        if (playerForBreathe != null) {
            return playerForBreathe.getCurrentPosition() / 1000;
        }
        return 0;
    }
}
