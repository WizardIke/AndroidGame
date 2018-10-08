package wizardike.assignment3.physics;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

public class CollisionHandlerLoader {
    interface Loader {
        CollisionHandler load(DataInputStream save) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    //static {
        // Add all collision handlers here
    //}

    public static CollisionHandler load(int id, DataInputStream save) throws IOException {
        return loaders.get(id).load(save);
    }
}
