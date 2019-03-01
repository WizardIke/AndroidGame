package wizardike.assignment3.userInterface;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.levels.Level;

public class AnalogStickListenerLoader {
    interface Loader {
        AnalogStickListener load(DataInputStream save, int entity, Level level, Deserializer deserializer) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    public static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        PlayerMovementController.registerLoader();
        PlayerAttackController.registerLoader();
    }

    public static AnalogStickListener load(int id, DataInputStream save, int entity, Level level, Deserializer deserializer) throws IOException {
        return loaders.get(id).load(save, entity, level, deserializer);
    }
}
