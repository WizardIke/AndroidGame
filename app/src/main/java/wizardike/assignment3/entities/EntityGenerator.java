package wizardike.assignment3.entities;

import android.util.SparseArray;

import wizardike.assignment3.Engine;

/**
 * Used to create entities based on their id if they aren't in the save file.
 * Can be used to e.g. randomly generate a level when it is first needed.
 */
public class EntityGenerator {
    public interface Callback {
        void onLoadComplete(Entity entity);
    }
    interface IEntityGenerator {
        void generateEntity(Engine engine, Callback callback);
    }
    private static final SparseArray<IEntityGenerator> entityGenerators = new SparseArray<>();

    static void addEntityGenerator(int id, IEntityGenerator generator) {
        entityGenerators.put(id, generator);
    }

    public static void generateEntity(int entityID, Engine engine, Callback callback) {
        entityGenerators.get(entityID).generateEntity(engine, callback);
    }
}
