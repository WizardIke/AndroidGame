package wizardike.assignment3;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseArray;

import java.io.Closeable;

/**
 * Can be used to load, unload and plays sounds.
 * If a sound is already loaded when a load request for it is made the loaded version of
 * the sound will be returned.
 * Note, callbacks will be on the ui thread
 */
public class AudioManager implements /*AutoCloseable*/ Closeable {

    public static abstract class Request {
        Request next;

        protected abstract void onLoadComplete(int soundId);
    }

    private static class TrackDescriptor {
        final int soundId;
        int referenceCount;
        Request requests;

        TrackDescriptor(int soundId, Request request) {
            this.soundId = soundId;
            referenceCount = 1;
            request.next = null;
            requests = request;
        }

        void addRequest(Request request) {
            request.next = requests;
            requests = request;
        }

        boolean isLoaded() {
            return requests == null;
        }

        public void onLoadComplete() {
            while(requests != null) {
                requests.onLoadComplete(soundId);
                requests = requests.next;
            }
        }
    }

    private SoundPool soundPool;
    private final Activity activity;
    private SparseArray<TrackDescriptor> descriptorsByResourceID = new SparseArray<>();
    private SparseArray<TrackDescriptor> descriptorsBySoundID = new SparseArray<>();

    @SuppressWarnings( "deprecation" )
    public AudioManager(final Activity activity, int maxStreams) {
        this.activity = activity;

        if(Build.VERSION.SDK_INT >= 21) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, android.media.AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                final int index = descriptorsBySoundID.indexOfKey(soundId);
                TrackDescriptor descriptor = descriptorsBySoundID.valueAt(index);
                descriptor.onLoadComplete();
                descriptorsBySoundID.removeAt(index);
            }
        });
    }

    //returns soundId in a call to loadedCallback
    public void load(final int resourceID, final Request loadedCallback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = descriptorsByResourceID.indexOfKey(resourceID);
                if(index >= 0) {
                    TrackDescriptor descriptor = descriptorsByResourceID.valueAt(index);
                    descriptor.referenceCount += 1;
                    int soundId = descriptor.soundId;
                    if(descriptor.isLoaded()) {
                        loadedCallback.onLoadComplete(soundId);
                    } else {
                        descriptor.addRequest(loadedCallback);
                    }
                } else {
                    int soundId = soundPool.load(activity, resourceID, 1);
                    TrackDescriptor descriptor = new TrackDescriptor(soundId, loadedCallback);
                    descriptorsByResourceID.put(resourceID, descriptor);
                    descriptorsBySoundID.put(soundId, descriptor);
                }
            }
        });
    }

    public void unload(final int resourceID) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int index = descriptorsByResourceID.indexOfKey(resourceID);
                final TrackDescriptor descriptor = descriptorsByResourceID.valueAt(index);
                descriptor.referenceCount -= 1;
                if(descriptor.referenceCount == 0){
                    final int soundId = descriptor.soundId;
                    soundPool.unload(soundId);
                    descriptorsByResourceID.removeAt(index);
                }
            }
        });
    }

    //returns streamID
    //might need to be on ui thread
    public int play(final int soundId, final float leftVolume, final float rightVolume,
                    final int priority, final int loop, final float rate) {
        return soundPool.play(soundId, leftVolume, rightVolume, priority, loop, rate);
    }

    //might need to be on ui thread
    public void stop(int streamID) {
        soundPool.stop(streamID);
    }

    @Override
    public void close() {
        soundPool.release();
    }
}
