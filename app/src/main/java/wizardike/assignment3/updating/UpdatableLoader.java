package wizardike.assignment3.updating;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;

public class UpdatableLoader {
    interface Loader {
        Updatable load(DataInputStream save, Deserializer deserializer) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    public static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        PlayerAttackUpdater.registerLoader();
   }

    public static Updatable load(int id, DataInputStream save, Deserializer deserializer) throws IOException {
        return loaders.get(id).load(save, deserializer);
    }
}
