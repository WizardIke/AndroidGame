package wizardike.assignment3.entities;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;

public class EntityLoader {
    public interface EntityLoadedCallback {
        void onLoadComplete(Entity entity);
    }
    interface IEntityLoader {
        void loadEntity(DataInputStream save, Engine world, EntityLoadedCallback callback) throws IOException;
    }
    private static final SparseArray<IEntityLoader> entityLoaders = new SparseArray<>();

    static void addEntityLoader(int id, IEntityLoader loader) {
        entityLoaders.put(id, loader);
    }

    public static void loadEntity(int id, DataInputStream save, Engine world,
                                  EntityLoadedCallback callback) throws IOException {
        entityLoaders.get(id).loadEntity(save, world, callback);
    }
}
