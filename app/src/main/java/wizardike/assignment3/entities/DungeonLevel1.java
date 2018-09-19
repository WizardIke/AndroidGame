package wizardike.assignment3.entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import wizardike.assignment3.Engine;
import wizardike.assignment3.Savable;
import wizardike.assignment3.Startable;

public class DungeonLevel1 implements Entity, Startable, Savable {
    public static final int id = 5;

    static {
        EntityLoader.addEntityLoader(id, new EntityLoader.IEntityLoader() {
            @Override
            public void loadEntity(DataInputStream save, Engine engine,
                                   EntityLoader.EntityLoadedCallback callback) throws IOException {
                new DungeonLevel1(save, engine, callback);
            }
        });

        EntityGenerator.addEntityGenerator(id, new EntityGenerator.IEntityGenerator() {
            @Override
            public void generateEntity(Engine engine, EntityGenerator.Callback callback) {
                new DungeonLevel1(engine, callback);
            }
        });
    }

    private final List<Entity> entities = new ArrayList<>();

    private DungeonLevel1(DataInputStream save, Engine engine,
                          final EntityLoader.EntityLoadedCallback callback) throws IOException {
        final int numberOfEntities = save.readInt();
        final AtomicInteger numberOfEntitiesLoaded = new AtomicInteger(0);
        final EntityLoader.EntityLoadedCallback callback2 = new EntityLoader.EntityLoadedCallback() {
            @Override
            public void onLoadComplete(Entity entity) {
                synchronized (entities) {
                    entities.add(entity);
                }
                final int currentNumberOfEntitiesLoaded = numberOfEntitiesLoaded.addAndGet(1);
                if(currentNumberOfEntitiesLoaded == numberOfEntities) {
                    callback.onLoadComplete(DungeonLevel1.this);
                }
            }
        };
        for(int i = 0; i < numberOfEntities; ++i) {
            final int entityID = save.readInt();
            EntityLoader.loadEntity(entityID, save, engine, callback2);
        }
    }

    private DungeonLevel1(Engine engine, EntityGenerator.Callback callback) {
        //TODO generate level
        callback.onLoadComplete(this);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    @Override
    public <T> T getComponent(Class<T> componentType) {
        return null;
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeInt(entities.size()); //TODO might be wrong size
        for(Entity entity : entities) {
            Savable saveFunc = entity.getComponent(Savable.class);
            if(saveFunc != null) {
                saveFunc.save(save);
            }
        }
    }

    @Override
    public void start(Engine engine) {
        for(Entity entity : entities) {
            Startable starter = entity.getComponent(Startable.class);
            if(starter != null) {
                starter.start(engine);
            }
        }
    }

    @Override
    public void stop(Engine engine) {
        for(Entity entity : entities) {
            Startable stopper = entity.getComponent(Startable.class);
            if(stopper != null) {
                stopper.stop(engine);
            }
        }
    }
}
