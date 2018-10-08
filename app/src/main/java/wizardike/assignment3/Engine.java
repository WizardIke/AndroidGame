package wizardike.assignment3;

import android.app.Activity;
import android.net.Uri;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import wizardike.assignment3.entities.EntityAllocator;
import wizardike.assignment3.graphics.GraphicsManager;
import wizardike.assignment3.worlds.MainWorld;
import wizardike.assignment3.worlds.World;
import wizardike.assignment3.worlds.WorldLoader;

public class Engine {
    private static final int MAX_AUDIO_STREAMS = 16;

    private PlayGameRequest playGameRequest;
    private GraphicsManager graphicsManager;
    private AudioManager audioManager;
    private WorkerThread workerThread = new WorkerThread();
    private EntityAllocator entityAllocator = new EntityAllocator();

    private LoadingScreen loadingScreen = null;
    private MainWorld mainWorld;

    public Engine(Activity context, PlayGameRequest playGameRequest) {
        graphicsManager = new GraphicsManager(context);
        graphicsManager.setEngine(this);
        audioManager = new AudioManager(context, MAX_AUDIO_STREAMS);
        workerThread.start();
        this.playGameRequest = playGameRequest;
        //create the loading screen
        new LoadingScreen(this, new LoadingScreen.Callback() {
            @Override
            public void onLoadComplete(LoadingScreen loadingScreen) {
                Engine.this.loadingScreen = loadingScreen;
                loadingScreenReady();
            }
        });
    }

    private void loadingScreenReady() {
        graphicsManager.queueEvent(new Runnable() {
            @Override
            public void run() {
                //display the loading screen
                loadingScreen.start(Engine.this);
                workerThread.addTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loadWorld(playGameRequest.saveFile, new WorldLoader.Callback() {
                                @Override
                                public void onLoadComplete(World mainWorld) { //will be called on the graphics thread
                                    Engine.this.mainWorld = (MainWorld)mainWorld;
                                    graphicsManager.addWorld(mainWorld);
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
        //Remove the loading screen
        loadingScreen.stop(Engine.this);
        loadingScreen = null; //delete loading screen
    }

    /**
     * Loads a saved game world from a file
     */
    private void loadWorld(Uri saveFileUri, WorldLoader.Callback callback) throws Exception {
        DataInputStream save = null;
        try {
            File saveFile = new File(saveFileUri.getPath());
            save = new DataInputStream(new FileInputStream(saveFile));
            new MainWorld(save, this, callback);
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
            mainWorld.save(save);
        } finally {
            if(save != null) {
                save.close();
            }
        }
    }

    public GraphicsManager getGraphicsManager() {
        return graphicsManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public WorkerThread getWorkerThread() {
        return workerThread;
    }

    public void resume() {
        graphicsManager.onResume();
    }

    public void pause() {
        graphicsManager.onPause();
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
        graphicsManager.queueEvent(new Runnable() {
            @Override
            public void run() {
                if(loadingScreen != null) {
                    loadingScreen.stop(Engine.this);
                    loadingScreen = null;
                }
                if(mainWorld != null) {
                    graphicsManager.removeWorld(mainWorld);
                    mainWorld = null;
                }
                workerThread.interrupt();
                audioManager.close();
                playGameRequest.onPlayingEnded(state);
            }
        });
    }

    public EntityAllocator getEntityAllocator() {
        return entityAllocator;
    }
}
