package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector4;
import wizardike.assignment3.graphics.Sprite;

/**
 * Handles saving and loading sprite sheets
 */
public class SpriteSheetSystem {
    public interface Callback {
        void onLoadComplete(SpriteSheetSystem spriteSheetSystem);
    }

    private ComponentStorage<SpriteSheet> spriteSheetComponentStorage;

    public SpriteSheetSystem() {
        spriteSheetComponentStorage = new ComponentStorage<>(SpriteSheet.class);
    }

    public SpriteSheetSystem(final DataInputStream save, final Engine engine, final Deserializer deserializer,
                             final Sprite[][] spritesToRemap, final int[] spritesToRemapLengths, final Callback callback) throws IOException {

        final int spriteSheetCount = save.readInt();
        if(spriteSheetCount == 0) {
            spriteSheetComponentStorage = new ComponentStorage<>(SpriteSheet.class);
            callback.onLoadComplete(this);
            return;
        }
        //load sprite sheets and create remapping table
        final HashMap<Vector4, Vector4> remappingTable = new HashMap<>();
        final SpriteSheet[] spriteSheets = new SpriteSheet[spriteSheetCount];
        final SpriteSheetLoader.Callback componentLoadedCallback = new SpriteSheetLoader.Callback() {
            private AtomicInteger loadedCount = new AtomicInteger(0);

            @Override
            public void onLoadComplete(SpriteSheet spriteSheet) {
                if(loadedCount.incrementAndGet() == spriteSheetCount) {
                    remapSprites(remappingTable, spritesToRemap, spritesToRemapLengths);

                    final int[] entities = new int[spriteSheetCount];
                    for(int i = 0; i != spriteSheetCount; ++i) {
                        try {
                            entities[i] = deserializer.getEntity(save.readInt());
                            deserializer.addObject(spriteSheets[i]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    spriteSheetComponentStorage = new ComponentStorage<>(SpriteSheet.class, entities, spriteSheets);
                    callback.onLoadComplete(SpriteSheetSystem.this);
                }
            }
        };
        for(int i = 0; i != spriteSheetCount; ++i) {
            final int id = save.readInt();
            spriteSheets[i] = SpriteSheetLoader.load(id, save, engine, remappingTable, componentLoadedCallback);
        }
    }

    public void addSpriteSheet(int entity, SpriteSheet spriteSheet) {
        spriteSheetComponentStorage.addComponent(entity, spriteSheet);
    }

    public void removeSpriteSheets(int entity) {
        spriteSheetComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        SpriteSheet[] spriteSheets = spriteSheetComponentStorage.getAllComponents();
        final int spriteSheetCount = spriteSheetComponentStorage.size();
        save.writeInt(spriteSheetCount);
        for(int i = 0; i != spriteSheetCount; ++i) {
            SpriteSheet spriteSheet = spriteSheets[i];
            save.writeInt(spriteSheet.getId());
            spriteSheet.save(save);
            serializer.addObject(spriteSheet);
        }
        final int[] spriteSheetEntities = spriteSheetComponentStorage.getAllEntities();
        for (int i = 0; i != spriteSheetCount; ++i) {
            save.writeInt(spriteSheetEntities[i]);
        }
    }

    private void remapSprites(HashMap<Vector4, Vector4> remappingTable, Sprite[][] spritesToRemap,
                              int[] spritesToRemapLength) {
        for(int i = 0; i != spritesToRemap.length; ++i) {
            Sprite[] sprites = spritesToRemap[i];
            final int spriteCount = spritesToRemapLength[i];
            for(int j = 0; j != spriteCount; ++j) {
                Sprite sprite = sprites[j];
                //remap sprite
                Vector4 newCoordinates = remappingTable.get(
                        new Vector4(sprite.texU, sprite.texV, sprite.texWidth, sprite.texHeight)
                );
                sprite.texU = newCoordinates.getX();
                sprite.texV = newCoordinates.getY();
                sprite.texWidth = newCoordinates.getZ();
                sprite.texHeight = newCoordinates.getW();
            }
        }
    }

    public SpriteSheet[] getSpriteSheets() {
        return spriteSheetComponentStorage.getAllComponents();
    }
}
