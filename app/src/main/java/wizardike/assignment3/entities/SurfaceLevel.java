package wizardike.assignment3.entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import wizardike.assignment3.Engine;
import wizardike.assignment3.EntityStreamingManager;

public class SurfaceLevel {
    public static final int id = 1;
    private static class LoadData {
        public EntityLoader.EntityLoadedCallback callback;
        final AtomicInteger numResourcesLoaded = new AtomicInteger(0);
        public Entity player;
        public DungeonLevel1 dungeonLevel1;
    }

    static {
        EntityLoader.addEntityLoader(id, new EntityLoader.IEntityLoader() {
            @Override
            public void loadEntity(DataInputStream save, Engine engine,
                                   EntityLoader.EntityLoadedCallback callback) throws IOException {
                final int numberOfEntities = save.readInt();
                if(numberOfEntities == 1) {
                    final int entityID = save.readInt();
                    final LoadData data = new LoadData();
                    data.callback = callback;
                    EntityLoader.loadEntity(entityID, save, engine, new EntityLoader.EntityLoadedCallback() {
                        @Override
                        public void onLoadComplete(Entity player) {
                            data.player = player;
                            componentLoaded(data);
                        }
                    });
                    //generate dungeon level 1
                    engine.getEntityStreamingManager().loadEntity(DungeonLevel1.id,
                            new EntityStreamingManager.Request() {
                        @Override
                        protected void onLoadComplete(Entity entity) {
                            data.dungeonLevel1 = (DungeonLevel1)entity;
                            componentLoaded(data);
                        }
                    });
                } else {
                    throw new IOException("Invalid save file");
                }
            }
        });
    }

    private static void componentLoaded(LoadData data) {
        int numComponentsLoaded = data.numResourcesLoaded.addAndGet(1);
        if(numComponentsLoaded == 2) {
            finishLoading(data);
        }
    }

    private static void finishLoading(LoadData data) {
        //add player to dungeon level 1
        data.dungeonLevel1.addEntity(data.player);
        //return dungeon level 1
        data.callback.onLoadComplete(data.dungeonLevel1);
    }

    public static int saveLength() {
        return Player.saveLength() + 4;
    }

    public static void generateSave(DataOutputStream save) throws IOException {
        save.writeInt(id);
        save.writeInt(1); //number of entities on level
        Player.generateSave(save);
    }
}
