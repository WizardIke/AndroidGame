package wizardike.assignment3.physics.Collision.CollisionHandlers;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.geometry.Vector2;

public class CollisionHandlerLoader {
    interface Loader {
        CollisionHandler load(DataInputStream save, Vector2[] positionRemappingTable) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        Bite.registerLoader();
        BiteClient.registerLoader();
        Explode.registerLoader();
        ExplodeHost.registerLoader();
        ExplodeClient.registerLoader();
    }

    public static CollisionHandler load(int id, DataInputStream save, Vector2[] positionRemappingTable) throws IOException {
        return loaders.get(id).load(save, positionRemappingTable);
    }
}
