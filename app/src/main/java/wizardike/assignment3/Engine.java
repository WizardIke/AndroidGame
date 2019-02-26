package wizardike.assignment3;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.fragments.UserInterface;
import wizardike.assignment3.graphics.GraphicsManager;
import wizardike.assignment3.networking.NetworkConnection;
import wizardike.assignment3.worlds.FrameTimer;
import wizardike.assignment3.worlds.MainWorld;
import wizardike.assignment3.worlds.World;
import wizardike.assignment3.worlds.WorldLoader;

public class Engine {
    private static final int MAX_AUDIO_STREAMS = 16;

    private PlayGameRequest playGameRequest;
    private GraphicsManager graphicsManager;
    private AudioManager audioManager;
    private ThreadPoolExecutor backgroundWorkManager;
    private EntityAllocator entityAllocator = new EntityAllocator();
    private FrameTimer frameTimer = new FrameTimer();
    private NetworkConnection networkConnection = null;
    private Random randomNumberGenerator = new Random();
    private UserInterface userInterface;

    private LoadingScreen loadingScreen = null;
    private MainWorld mainWorld;

    public Engine(Activity context, PlayGameRequest playGameRequest) {
        graphicsManager = new GraphicsManager(context);
        graphicsManager.setEngine(this);
        audioManager = new AudioManager(context, MAX_AUDIO_STREAMS);
        backgroundWorkManager = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                1,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
        this.playGameRequest = playGameRequest;
        userInterface = new UserInterface();
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
                backgroundWorkManager.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loadWorld(playGameRequest.saveFile, new WorldLoader.Callback() {
                                @Override
                                public void onLoadComplete(final World mainWorld) { //will be called on the graphics thread
                                    synchronized (Engine.this) {
                                        Engine.this.mainWorld = (MainWorld) mainWorld;
                                    }
                                    graphicsManager.queueEvent(new Runnable() {
                                        @Override
                                        public void run() {
                                            frameTimer.start();
                                            graphicsManager.addWorld(frameTimer);

                                            graphicsManager.addWorld(Engine.this.mainWorld);
                                            loadingFinished();
                                        }
                                    });
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

        playGameRequest.addFragment(R.id.fragment_container, userInterface);
    }

    /**
     * Loads a saved game world from a file
     */
    private void loadWorld(Uri saveFileUri, final WorldLoader.Callback callback) throws Exception {
        File saveFile = new File(saveFileUri.getPath());
        final DataInputStream save = new DataInputStream(new FileInputStream(saveFile));
        try {
            new MainWorld(save, this, new WorldLoader.Callback() {
                @Override
                public void onLoadComplete(World world) {
                    try {
                        save.close();
                        callback.onLoadComplete(world);
                    } catch (IOException e) {
                        e.printStackTrace();
                        playGameRequest.onPlayingEnded(PlayGameRequest.GameState.error);
                    }
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
            playGameRequest.onPlayingEnded(PlayGameRequest.GameState.error);
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

    public FrameTimer getFrameTimer() {
        return frameTimer;
    }

    public GraphicsManager getGraphicsManager() {
        return graphicsManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public Executor getBackgroundWorkManager() {
        return backgroundWorkManager;
    }

    public Random getRandomNumberGenerator() {
        return randomNumberGenerator;
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    public void resume() {
        graphicsManager.onResume();
    }

    public void pause() {
        graphicsManager.onPause();
        synchronized (this) {
            if(mainWorld != null) {
                try {
                    save(playGameRequest.saveFile);
                } catch (Exception e) {
                    playingEnded(PlayGameRequest.GameState.error);
                }
            }
        }
    }

    /**
     * will eventually stop the game playing
     */
    private void playingEnded(final PlayGameRequest.GameState state) {
        if(state == PlayGameRequest.GameState.error) {
            Log.e("Engine", "playingEnded called with state " + state);
        } else {
            Log.i("Engine", "playingEnded called with state " + state);
        }
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
                backgroundWorkManager.shutdown();
                audioManager.close();
                playGameRequest.onPlayingEnded(state);
            }
        });
    }

    public EntityAllocator getEntityAllocator() {
        return entityAllocator;
    }

    public void onWin() throws IOException {
        //TODO
        playingEnded(PlayGameRequest.GameState.finished);
    }

    public void onLoose() throws IOException {
        //TODO
        playingEnded(PlayGameRequest.GameState.finished);
    }

    public void onError() {
        playingEnded(PlayGameRequest.GameState.error);
    }

    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

    public void setNetworkConnection(NetworkConnection networkConnection) {
        this.networkConnection = networkConnection;
    }

    public MainWorld getMainWorld() {
        return mainWorld;
    }
}
