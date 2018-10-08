package wizardike.assignment3.physics;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

public class CollidableLoader {
    interface Loader {
        Collidable load(DataInputStream save) throws IOException;
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

    public static Collidable load(int id, DataInputStream save) throws IOException {
        return loaders.get(id).load(save);
    }
}
