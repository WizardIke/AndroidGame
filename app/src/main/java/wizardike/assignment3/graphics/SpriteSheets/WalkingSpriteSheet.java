package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector4;
import wizardike.assignment3.graphics.TextureManager;

public abstract class WalkingSpriteSheet implements SpriteSheet {
    private final int resourceId;
    public float[] xCoordinates;
    public float[] yCoordinates;
    public float spriteTextureWidth;
    public float spriteTextureHeight;

    public WalkingSpriteSheet(int resourceId, Engine engine, final SpriteSheetLoader.Callback callback) {
        this.resourceId = resourceId;
        engine.getGraphicsManager().getTextureManager().loadTexture(resourceId, new TextureManager.Request() {
            @Override
            protected void onLoadComplete(Vector4 textureCoordinates) {
                calculateCoordinates(textureCoordinates);
                callback.onLoadComplete(WalkingSpriteSheet.this);
            }
        });
    }

    public WalkingSpriteSheet(DataInputStream save, Engine engine,
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
                callback.onLoadComplete(WalkingSpriteSheet.this);
            }
        });
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeInt(resourceId);
        save.writeFloat(xCoordinates[0]);
        save.writeFloat(yCoordinates[0]);
        save.writeFloat(spriteTextureWidth);
        save.writeFloat(spriteTextureHeight);
    }

    protected void calculateCoordinates(Vector4 textureCoordinates) {
        xCoordinates = new float[36];
        yCoordinates = new float[36];
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
    }

    private void populateRemappingTable(float oldX, float oldY, float oldWidth, float oldHeight,
                                        final HashMap<Vector4, Vector4> remappingTable) {
        float newX = xCoordinates[0];
        float newY = yCoordinates[0];
        float widthMultiplier = oldWidth / spriteTextureWidth;
        float heightMultiplier = oldHeight / spriteTextureHeight;
        final int length = xCoordinates.length;
        synchronized (remappingTable) {
            for(int i = 0; i != length; ++i) {
                Vector4 oldPosition = new Vector4(
                        (xCoordinates[i] - newX) * widthMultiplier + oldX,
                        (yCoordinates[i] - newY) * heightMultiplier + oldY,
                        oldWidth,
                        oldHeight);
                Vector4 newPosition = new Vector4(xCoordinates[i], yCoordinates[i],
                        spriteTextureWidth, spriteTextureHeight);
                remappingTable.put(oldPosition, newPosition);
            }
        }
    }
}
