package wizardike.assignment3.health;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

public class HealthHostLoader {
    interface Loader {
        HealthHost load(DataInputStream save) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        HealthHost.registerLoader();
        SkeletonHealthHost.registerLoader();
    }

    public static HealthHost load(int id, DataInputStream save) throws IOException {
        return loaders.get(id).load(save);
    }
}
