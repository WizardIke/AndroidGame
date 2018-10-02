package wizardike.assignment3.entities;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.EntityStreamingManager;
import wizardike.assignment3.Savable;
import wizardike.assignment3.Startable;
import wizardike.assignment3.graphics.UpdateListener;
import wizardike.assignment3.physics.CollisionSystem;
import wizardike.assignment3.UpdatingSystem;

public class World implements Entity, Savable, Startable, UpdateListener {
    private static final String TAG = "World";
    public static final int id = 0;

    static {
        EntityLoader.addEntityLoader(id, new EntityLoader.IEntityLoader() {
            @Override
            public void loadEntity(DataInputStream save, Engine engine,
                                     EntityLoader.EntityLoadedCallback callback) throws IOException {
                new World(save, engine, callback);
            }
        });
        Log.d(TAG, "World class loaded");
    }

    private int levelID;
    private Entity level;
    private Engine engine;

    private UpdatingSystem updatingSystem;
    private CollisionSystem collisionSystem;

    private World(DataInputStream save, final Engine engine,
                  final EntityLoader.EntityLoadedCallback callback) throws IOException {
        this.engine = engine;
        updatingSystem = new UpdatingSystem(engine);
        collisionSystem = new CollisionSystem(engine);

        levelID = save.readInt();
        engine.getEntityStreamingManager().addEntityData(save);
        engine.getEntityStreamingManager().loadEntity(levelID, new EntityStreamingManager.Request() {
            @Override
            protected void onLoadComplete(Entity entity) {
                level = entity;
                //the world has finished loading
                callback.onLoadComplete(World.this);
            }
        });
    }

    public void setLevel(int levelID, Entity level) {
        this.levelID = levelID;
        this.level = level;
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeInt(levelID);
        engine.getEntityStreamingManager().save(save);
    }

    @Override
    public <T> T getComponent(Class<T> componentType) {
        return null;
    }

    @Override
    public void start(Engine engine) {
        Startable startFunc = level.getComponent(Startable.class);
        if(startFunc != null) {
            startFunc.start(engine);
        }
    }

    @Override
    public void stop(Engine engine) {
        Startable startFunc = level.getComponent(Startable.class);
        if(startFunc != null) {
            startFunc.stop(engine);
        }
    }

    public UpdatingSystem getUpdatingSystem() {
        return updatingSystem;
    }

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    @Override
    public void update() {
        updatingSystem.update();
        collisionSystem.update();
        //TODO update geometry system
        //TODO update lighting system which updates shadow system when needed
    }
}
