package wizardike.assignment3.worlds;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.PlayerInfo;
import wizardike.assignment3.assemblies.EntityLoadedCallback;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.levels.LevelStreamingManager;

public class MainWorld implements World {
    public static final int id = 0;

    private int levelId;
    private Level level;
    private LevelStreamingManager levelStreamingManager;
    private final PlayerInfo playerInfo;
    private SparseArray<Level> levelsById = new SparseArray<>();
    private IdentityHashMap<Level, Integer> idsByLevel = new IdentityHashMap<>();

    static void registerLoader() {
        WorldLoader.addLoader(id, new WorldLoader.Loader() {
            @Override
            public World load(DataInputStream save, final Engine engine,
                                  final WorldLoader.Callback callback) throws IOException {
                return new MainWorld(save, engine, callback);
            }
        });
    }

    public MainWorld(DataInputStream save, final Engine engine,
                      final WorldLoader.Callback callback) throws IOException {
        final int saveState = save.readInt();
        playerInfo = new PlayerInfo(save);
        levelId = save.readInt();
        levelStreamingManager = new LevelStreamingManager(engine, save);

        levelStreamingManager.loadLevel(levelId, new LevelStreamingManager.Request() {
            @Override
            protected void onLoadComplete(Level level) {
                MainWorld.this.level = level;
                addLevel(levelId, level);
                if(saveState == 0) {
                    //this is the first time this save has been loaded, create the player
                    playerInfo.createPlayer(engine, level, new EntityLoadedCallback() {
                        @Override
                        public void onLoadComplete(int mageEntity) {
                            //the world has finished loading
                            callback.onLoadComplete(MainWorld.this);
                        }
                    });
                } else {
                    //the world has finished loading
                    callback.onLoadComplete(MainWorld.this);
                }
            }
        });
    }

    public void setCurrentLevel(int levelId, Level level) {
        this.levelId = levelId;
        this.level = level;
    }

    public void addLevel(int levelId, Level level) {
        levelsById.put(levelId, level);
        idsByLevel.put(level, levelId);
    }

    public int getIdOfLevel(Level level) {
        return idsByLevel.get(level);
    }

    public Level getLevelById(int id) {
        return levelsById.get(id);
    }

    public LevelStreamingManager getLevelStreamingManager() {
        return levelStreamingManager;
    }

    @Override
    public void update(Engine engine) {
        level.update(engine);
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeInt(1); //1 means the save has been loaded before
        playerInfo.save(save);
        save.writeInt(levelId);
        levelStreamingManager.save(save);
    }

    @Override
    public int getId() {
        return id;
    }
}
