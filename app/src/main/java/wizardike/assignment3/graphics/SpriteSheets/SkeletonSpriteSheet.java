package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector4;

public class SkeletonSpriteSheet extends WalkingSpriteSheet {
    private static final int id = 0;

    static void registerLoader() {
        SpriteSheetLoader.addLoader(id, new SpriteSheetLoader.Loader() {
            @Override
            public SpriteSheet load(DataInputStream save, Engine engine, HashMap<Vector4, Vector4> remappingTable, SpriteSheetLoader.Callback callback) throws IOException {
                return new SkeletonSpriteSheet(save, engine, remappingTable, callback);
            }
        });
    }

    public SkeletonSpriteSheet(int resourceId, Engine engine, final SpriteSheetLoader.Callback callback) {
        super(resourceId, engine, callback);
    }

    public SkeletonSpriteSheet(DataInputStream save, Engine engine,
                               final HashMap<Vector4, Vector4> remappingTable,
                               final SpriteSheetLoader.Callback callback) throws IOException {
        super(save, engine, remappingTable, callback);
    }

    @Override
    public int getId() {
        return id;
    }
}
