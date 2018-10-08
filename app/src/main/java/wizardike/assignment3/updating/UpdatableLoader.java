package wizardike.assignment3.updating;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

public class UpdatableLoader {
    interface Loader {
        Updatable load(DataInputStream save) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    public static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    //static {

   //}

    public static Updatable load(int id, DataInputStream save) throws IOException {
        return loaders.get(id).load(save);
    }
}
