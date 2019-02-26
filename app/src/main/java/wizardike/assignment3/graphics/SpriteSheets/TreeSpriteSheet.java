package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector4;
import wizardike.assignment3.graphics.TextureManager;

public class TreeSpriteSheet implements SpriteSheet {
    private static final int id = 3;

    private final int resourceId;
    public float[] xCoordinates;
    public float[] yCoordinates;
    public float[] spriteTextureWidths;
    public float[] spriteTextureHeights;
    private float totalWidth;
    private float totalHeight;

    static void registerLoader() {
        SpriteSheetLoader.addLoader(id, new SpriteSheetLoader.Loader() {
            @Override
            public SpriteSheet load(DataInputStream save, Engine engine, HashMap<Vector4, Vector4> remappingTable, SpriteSheetLoader.Callback callback) throws IOException {
                return new TreeSpriteSheet(save, engine, remappingTable, callback);
            }
        });
    }

    public TreeSpriteSheet(int resourceId, Engine engine, final SpriteSheetLoader.Callback callback) {
        this.resourceId = resourceId;
        engine.getGraphicsManager().getTextureManager().loadTexture(resourceId, new TextureManager.Request() {
            @Override
            protected void onLoadComplete(Vector4 textureCoordinates) {
                calculateCoordinates(textureCoordinates);
                callback.onLoadComplete(TreeSpriteSheet.this);
            }
        });
    }

    public TreeSpriteSheet(DataInputStream save, Engine engine,
                               final HashMap<Vector4, Vector4> remappingTable,
                               final SpriteSheetLoader.Callback callback) throws IOException {
        this.resourceId = save.readInt();
        final float oldX = save.readFloat();
        final float oldY = save.readFloat();
        final float oldWidth = save.readFloat();
        final float oldHeight = save.readFloat();
        engine.getGraphicsManager().getTextureManager().loadTexture(resourceId, new TextureManager.Request() {
            @Override
            protected void onLoadComplete(Vector4 textureCoordinates) {
                calculateCoordinates(textureCoordinates);
                populateRemappingTable(oldX, oldY, oldWidth, oldHeight, remappingTable);
                callback.onLoadComplete(TreeSpriteSheet.this);
            }
        });
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeInt(resourceId);
        save.writeFloat(xCoordinates[0]);
        save.writeFloat(yCoordinates[0]);
        save.writeFloat(totalWidth);
        save.writeFloat(totalHeight);
    }

    @Override
    public int getId() {
        return id;
    }

    private void calculateCoordinates(Vector4 textureCoordinates) {
        xCoordinates = new float[3];
        yCoordinates = new float[3];
        spriteTextureWidths = new float[3];
        spriteTextureHeights = new float[3];
        float x = textureCoordinates.getX();
        float y = textureCoordinates.getY();
        totalWidth = textureCoordinates.getZ();
        totalHeight = textureCoordinates.getW();

        xCoordinates[0] = x + 2.0f / 8.0f * textureCoordinates.getZ();
        yCoordinates[0] = y;
        spriteTextureWidths[0] = 3.0f / 8.0f * textureCoordinates.getZ();
        spriteTextureHeights[0] = 4.0f / 8.0f * textureCoordinates.getW();

        xCoordinates[1] = x;
        yCoordinates[1] = y + 5.0f / 8.0f * textureCoordinates.getW();
        spriteTextureWidths[1] = 2.0f / 8.0f * textureCoordinates.getZ();
        spriteTextureHeights[1] = 3.0f / 8.0f * textureCoordinates.getW();

        xCoordinates[2] = x;
        yCoordinates[2] = y;
        spriteTextureWidths[2] = 2.0f / 8.0f * textureCoordinates.getZ();
        spriteTextureHeights[2] = 5.0f / 8.0f * textureCoordinates.getW();
    }

    private void populateRemappingTable(float oldX, float oldY, float oldWidth, float oldHeight,
                                        final HashMap<Vector4, Vector4> remappingTable) {
        float newX = xCoordinates[0];
        float newY = yCoordinates[0];
        float widthMultiplier = oldWidth / totalWidth;
        float heightMultiplier = oldHeight / totalHeight;
        final int length = xCoordinates.length;
        synchronized (remappingTable) {
            for(int i = 0; i != length; ++i) {
                Vector4 oldPosition = new Vector4(
                        (xCoordinates[i] - newX) * widthMultiplier + oldX,
                        (yCoordinates[i] - newY) * heightMultiplier + oldY,
                        spriteTextureWidths[i] * widthMultiplier,
                        spriteTextureHeights[i] * heightMultiplier);
                Vector4 newPosition = new Vector4(xCoordinates[i], yCoordinates[i],
                        spriteTextureWidths[i], spriteTextureHeights[i]);
                remappingTable.put(oldPosition, newPosition);
            }
        }
    }
}
