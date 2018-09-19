package wizardike.assignment3;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.entities.EntityGenerator;
import wizardike.assignment3.entities.EntityLoader;
import wizardike.assignment3.graphics.GraphicsSystem;
import wizardike.assignment3.physics.PhysicsSystem;
import wizardike.assignment3.updating.UpdatingSystem;

public class Engine {
    private static final String TAG = "Engine";
    private static final int MAX_AUDIO_STREAMS = 16;

    private PlayGameRequest playGameRequest;
    private GraphicsSystem graphicsSystem;
    private UpdatingSystem updatingSystem;
    private PhysicsSystem physicsSystem;
    private AudioManager audioManager;
    private WorkerThread workerThread = new WorkerThread();
    private EntityStreamingManager entityStreamingManager;
    private LoadingScreen loadingScreen;
    private int worldId;
    private Entity world = null;

    public Engine(Activity context, PlayGameRequest playGameRequest) {
        graphicsSystem = new GraphicsSystem(context);
        updatingSystem = new UpdatingSystem(this);
        graphicsSystem.addUpdateListener(updatingSystem);
        physicsSystem = new PhysicsSystem(this);
        graphicsSystem.addUpdateListener(physicsSystem);
        audioManager = new AudioManager(context, MAX_AUDIO_STREAMS);
        workerThread.start();
        entityStreamingManager = new EntityStreamingManager(this);
        this.playGameRequest = playGameRequest;
        //create the loading screen
        loadingScreen = new LoadingScreen(this, new EntityGenerator.Callback() {
            @Override
            public void onLoadComplete(Entity entity) {
                loadingScreenReady();
            }
        });
    }

    private void loadingScreenReady() {
        graphicsSystem.queueEvent(new Runnable() {
            @Override
            public void run() {
                //display the loading screen
                loadingScreen.start(Engine.this);
                workerThread.addTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loadWorld(playGameRequest.saveFile, new EntityLoader.EntityLoadedCallback() {
                                @Override
                                public void onLoadComplete(Entity entity) {
                                    world = entity;
                                    loadingFinished();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            playGameRequest.onPlayingEnded(PlayGameRequest.GameState.error);
                        }
                    }
                });
            }
        });
    }

    private void loadingFinished() {
        graphicsSystem.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Remove the loading screen
                loadingScreen.stop(Engine.this);
                loadingScreen = null; //delete loading screen
                //Start the world updating and displaying
                Startable worldRunner = world.getComponent(Startable.class);
                if(worldRunner != null) {
                    worldRunner.start(Engine.this);
                }
            }
        });
    }

    /**
     * Loads a saved game world from a file
     */
    private void loadWorld(Uri saveFileUri, EntityLoader.EntityLoadedCallback callback) throws Exception {
        DataInputStream save = null;
        try {
            File saveFile = new File(saveFileUri.getPath());
            save = new DataInputStream(new FileInputStream(saveFile));

            worldId = save.readInt();
            Log.d(TAG, "WorldId: " + worldId);
            EntityLoader.loadEntity(worldId, save, this, callback);
        } finally {
            if(save != null) {
                save.close();
            }
        }
    }

    /**
     * Saves the game world to a file
     */
    private void save(Uri saveFileUri) throws Exception {
        File saveFile = new File(saveFileUri.getPath());

        DataOutputStream save = null;
        try {
            save = new DataOutputStream(new FileOutputStream(saveFile));
            Savable saveFunc = world.getComponent(Savable.class);
            if(saveFunc != null) {
                saveFunc.save(save);
            }
        } finally {
            if(save != null) {
                save.close();
            }
        }
    }

    public UpdatingSystem getUpdatingSystem() {
        return updatingSystem;
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    public GraphicsSystem getGraphicsSystem() {
        return graphicsSystem;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public WorkerThread getWorkerThread() {
        return workerThread;
    }

    public EntityStreamingManager getEntityStreamingManager() {
        return entityStreamingManager;
    }

    public void resume() {
        graphicsSystem.onResume();
    }

    public void pause() {
        graphicsSystem.onPause();
        try {
            save(playGameRequest.saveFile);
        } catch (Exception e) {
            playingEnded(PlayGameRequest.GameState.error);
        }
    }

    /**
     * will eventually stop the game playing
     */
    public void playingEnded(final PlayGameRequest.GameState state) {
        graphicsSystem.queueEvent(new Runnable() {
            @Override
            public void run() {
                if(world != null) {
                    Startable worldRunner = world.getComponent(Startable.class);
                    if(worldRunner != null) {
                        worldRunner.stop(Engine.this);
                    }
                    entityStreamingManager.unloadEntity(worldId);
                }
                workerThread.interrupt();
                graphicsSystem.removeUpdateListener(updatingSystem);
                graphicsSystem.removeUpdateListener(physicsSystem);
                audioManager.close();
                playGameRequest.onPlayingEnded(state);
            }
        });
    }
}
