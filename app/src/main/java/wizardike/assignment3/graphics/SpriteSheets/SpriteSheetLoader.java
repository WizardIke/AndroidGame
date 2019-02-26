package wizardike.assignment3.graphics.SpriteSheets;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector4;

public class SpriteSheetLoader {
    interface Callback {
        void onLoadComplete(SpriteSheet spriteSheet);
    }
    interface Loader {
        SpriteSheet load(DataInputStream save, Engine engine, final HashMap<Vector4, Vector4> remappingTable, Callback callback) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    public static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        SkeletonSpriteSheet.registerLoader();
        NecromancerSpriteSheet.registerLoader();
    }

    public static SpriteSheet load(int id, DataInputStream save, Engine engine,
                                   final HashMap<Vector4, Vector4> remappingTable, Callback callback)
            throws IOException {
        return loaders.get(id).load(save, engine, remappingTable, callback);
    }
}
