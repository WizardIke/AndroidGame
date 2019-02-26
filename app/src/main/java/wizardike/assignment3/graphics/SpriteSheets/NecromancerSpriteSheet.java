package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector4;

public class NecromancerSpriteSheet extends WalkingSpriteSheet {
    private static final int id = 1;

    static void registerLoader() {
        SpriteSheetLoader.addLoader(id, new SpriteSheetLoader.Loader() {
            @Override
            public SpriteSheet load(DataInputStream save, Engine engine, HashMap<Vector4, Vector4> remappingTable, SpriteSheetLoader.Callback callback) throws IOException {
                return new NecromancerSpriteSheet(save, engine, remappingTable, callback);
            }
        });
    }

    public NecromancerSpriteSheet(int resourceId, Engine engine, final SpriteSheetLoader.Callback callback) {
        super(resourceId, engine, callback);
    }

    public NecromancerSpriteSheet(DataInputStream save, Engine engine,
                               final HashMap<Vector4, Vector4> remappingTable,
                               final SpriteSheetLoader.Callback callback) throws IOException {
        super(save, engine, remappingTable, callback);
    }

    @Override
    public int getId() {
        return id;
    }
}
