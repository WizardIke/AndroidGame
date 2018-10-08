package wizardike.assignment3.worlds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.entities.Player;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.levels.LevelStreamingManager;

public class MainWorld implements World {
    public static final int id = 0;

    private int levelId;
    private Level level;
    private LevelStreamingManager levelStreamingManager;

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
        levelId = save.readInt();
        levelStreamingManager = new LevelStreamingManager(engine, save);
        levelStreamingManager.loadLevel(levelId, new LevelStreamingManager.Request() {
            @Override
            protected void onLoadComplete(Level level) {
                MainWorld.this.level = level;
                if(saveState == 0) {
                    //this is the first time this save has been loaded, create the player
                    Player.create(engine, level);
                }
                //the world has finished loading
                callback.onLoadComplete(MainWorld.this);
            }
        });
    }

    public void setLevel(int levelId, Level level) {
        this.levelId = levelId;
        this.level = level;
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
        save.writeInt(levelId);
        levelStreamingManager.save(save);
    }

    @Override
    public int getId() {
        return id;
    }
}
