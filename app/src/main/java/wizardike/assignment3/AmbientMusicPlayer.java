package wizardike.assignment3;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Plays a number of music resources at random.
 * All methods should be called on the ui thread
 */
public class AmbientMusicPlayer implements /*AutoCloseable*/ Closeable, MediaPlayer.OnCompletionListener {
    private int[] mFileIDs;
    private int mLastFileIDIndex;
    private final MediaPlayer mediaPlayer;
    private final Context mContext;

    //setAudioStreamType is now deprecated but it's replacement setAudioAttributes was only added in SDK level 21
    @SuppressWarnings( "deprecation" )
    public AmbientMusicPlayer(int[] fileIDs, Context context) {
        setFileIDs(fileIDs);
        mediaPlayer = new MediaPlayer();
        mContext = context;
        if(Build.VERSION.SDK_INT >= 21) {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mediaPlayer.setOnCompletionListener(this);
    }

    public void start() {
        mediaPlayer.reset();
        playNextMusic();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void close() {
        mediaPlayer.release();
    }

    public void changeFileIDs(int[] fileIDs) {
        boolean wasPlaying = mediaPlayer.isPlaying();
        mediaPlayer.reset();
        setFileIDs(fileIDs);
        if(wasPlaying) {playNextMusic();}
    }

    private void playNextMusic() {
        int resourceIDIndex;
        if(Build.VERSION.SDK_INT >= 21) {
            resourceIDIndex = ThreadLocalRandom.current().nextInt(0, mFileIDs.length - 1);
        } else {
            resourceIDIndex = (int)(Math.random() * mFileIDs.length - 1);
        }
        if(resourceIDIndex == mLastFileIDIndex) {
            resourceIDIndex = mFileIDs.length - 1;
        }
        mLastFileIDIndex = resourceIDIndex;
        final int resourceID = mFileIDs[resourceIDIndex];
        try {
            final AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(resourceID);
            if (afd != null) {
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
            } else {
                Log.d("AmbientMusicPlayer", "open file failed: " +
                        mContext.getResources().getResourceEntryName(resourceID));
                playNextMusic(); //failed, try different resource.
            }

        } catch (IOException ex) {
            Log.d("AmbientMusicPlayer", "set data source failed:", ex);
            playNextMusic();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        playNextMusic();
    }

    private void setFileIDs(int[] fileIDs) {
        mFileIDs = fileIDs;
        if(Build.VERSION.SDK_INT >= 21) {
            mLastFileIDIndex = ThreadLocalRandom.current().nextInt(0, mFileIDs.length);
        } else {
            mLastFileIDIndex = (int)(Math.random() * mFileIDs.length);
        }
    }

    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }
}
