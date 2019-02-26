package wizardike.assignment3.health;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

public class HealthComponentLoader {
    interface Loader {
        Health load(DataInputStream save) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        Health.registerLoader();
        NPCHealth.registerLoader();
        PlayerHealth.registerLoader();
        PlayerHealthHost.registerLoader();
    }

    public static Health load(int id, DataInputStream save) throws IOException {
        return loaders.get(id).load(save);
    }
}
