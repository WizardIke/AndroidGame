package wizardike.assignment3.worlds;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;

public class WorldLoader {
    public interface Callback {
        void onLoadComplete(World updatable);
    }
    interface Loader {
        World load(DataInputStream save, Engine engine, Callback callback) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    public static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        MainWorld.registerLoader();
        FrameTimer.registerLoader();
    }

    public static World load(int id, DataInputStream save, Engine engine, Callback callback) throws IOException {
        return loaders.get(id).load(save, engine, callback);
    }
}
