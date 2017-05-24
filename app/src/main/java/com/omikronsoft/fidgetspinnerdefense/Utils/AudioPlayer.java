package com.omikronsoft.fidgetspinnerdefense.Utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.omikronsoft.fidgetspinnerdefense.R;

/**
 * Created by Dariusz Lelek on 5/23/2017.
 * dariusz.lelek@gmail.com
 */

public class AudioPlayer {
    private static AudioPlayer instance;
    private final MediaPlayer ballHit, gameOver, spinning;

    public enum SoundName {
        BALL_HIT, SWEEP_SPIN, GAME_OVER, SPINNING
    }

    private AudioPlayer() {
        Context context = ApplicationContext.get();

        ballHit = MediaPlayer.create(context, R.raw.ballhit);
        gameOver = MediaPlayer.create(context, R.raw.gameover);
        spinning = MediaPlayer.create(context, R.raw.spinning);

        spinning.setVolume(0.2f, 0.2f);
        gameOver.setVolume(0.3f, 0.3f);
    }

    public void playSound(SoundName soundName) {
        if (Globals.getInstance().isSoundEnabled()) {
            switch (soundName) {
                case BALL_HIT:
                    play(ballHit, 0);
                    break;
                case GAME_OVER:
                    play(gameOver, 0);
                    break;
                case SPINNING:
                    play(spinning, 4000);
                    break;
                case SWEEP_SPIN:
                    play(spinning, 2000);
                    break;
                default:
                    break;
            }
        }
    }

    void stopSpinningSound() {
        if (spinning.isPlaying()) {
            spinning.pause();
        }
    }

    private void play(MediaPlayer player, int start) {
        if (player.isPlaying()) {
            player.pause();
            player.seekTo(start);
        }
        player.start();
    }

    public synchronized static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }
}
