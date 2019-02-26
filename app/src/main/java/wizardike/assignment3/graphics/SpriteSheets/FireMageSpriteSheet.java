package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector4;

public class FireMageSpriteSheet extends WalkingSpriteSheet {
    private static final int id = 2;

    static void registerLoader() {
        SpriteSheetLoader.addLoader(id, new SpriteSheetLoader.Loader() {
            @Override
            public SpriteSheet load(DataInputStream save, Engine engine, HashMap<Vector4, Vector4> remappingTable, SpriteSheetLoader.Callback callback) throws IOException {
                return new FireMageSpriteSheet(save, engine, remappingTable, callback);
            }
        });
    }

    public FireMageSpriteSheet(int resourceId, Engine engine, final SpriteSheetLoader.Callback callback) {
        super(resourceId, engine, callback);
    }

    public FireMageSpriteSheet(DataInputStream save, Engine engine,
                                  final HashMap<Vector4, Vector4> remappingTable,
                                  final SpriteSheetLoader.Callback callback) throws IOException {
        super(save, engine, remappingTable, callback);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    protected void calculateCoordinates(Vector4 textureCoordinates) {
        xCoordinates = new float[36 + 16];
        yCoordinates = new float[36 + 16];
        spriteTextureWidth = textureCoordinates.getZ() / 8.0f;
        spriteTextureHeight = textureCoordinates.getW() / 8.0f;
        float x = textureCoordinates.getX();
        float y = textureCoordinates.getY();
        for(int i = 0; i != 4; ++i) {
            for(int j = 0; j != 8; ++j) {
                xCoordinates[i * 9 + j] = x + spriteTextureWidth * j;
                yCoordinates[i * 9 + j] = y + spriteTextureHeight * i;
            }
            xCoordinates[i * 9 + 8] = x;
            yCoordinates[i * 9 + 8] = y + spriteTextureHeight * (i + 4);
        }
        for(int i = 0; i != 4; ++i) {
            for(int j = 0; j != 4; ++j) {
                xCoordinates[36 + i * 4 + j] = x + spriteTextureWidth * (j + 4);
                yCoordinates[36 + i * 4 + j] = y + spriteTextureHeight * (i + 4);
            }
        }
    }
}
