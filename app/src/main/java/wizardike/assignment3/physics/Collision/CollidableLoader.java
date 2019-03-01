package wizardike.assignment3.physics.Collision;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.geometry.Vector2;

public class CollidableLoader {
    interface Loader {
        Collidable load(DataInputStream save, Deserializer deserializer) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        AlignedRectangleHitBox.registerLoader();
        CircleHitBox.registerLoader();
        TriggeredCircleHitBox.registerLoader();
    }

    public static Collidable load(int id, DataInputStream save, Deserializer deserializer) throws IOException {
        return loaders.get(id).load(save, deserializer);
    }
}
