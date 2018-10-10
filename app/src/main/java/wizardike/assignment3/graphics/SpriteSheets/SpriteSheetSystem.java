package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entities.EntityUpdater;
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
        spriteSheetComponentStorage = new ComponentStorage<>();
    }

    public SpriteSheetSystem(final DataInputStream save, Engine engine, final EntityUpdater entityUpdater,
                             final Sprite[][] spritesToRemap, final Callback callback) throws IOException {
        //load sprite sheets and create remapping table
        final HashMap<Vector4, Vector4> remappingTable = new HashMap<>();
        final int spriteSheetCount = save.readInt();
        final SpriteSheet[] spriteSheets = new SpriteSheet[spriteSheetCount];
        final SpriteSheetLoader.Callback componentLoadedCallback = new SpriteSheetLoader.Callback() {
            private AtomicInteger loadedCount = new AtomicInteger(0);

            @Override
            public void onLoadComplete(SpriteSheet spriteSheet) {
                if(loadedCount.incrementAndGet() == spriteSheetCount) {
                    remapSprites(remappingTable, spritesToRemap);

                    final int[] entities = new int[spriteSheetCount];
                    for(int i = 0; i != spriteSheetCount; ++i) {
                        try {
                            entities[i] = save.readInt();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    spriteSheetComponentStorage = new ComponentStorage<>(entities, spriteSheets);
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

    public void save(DataOutputStream save) throws IOException {
        SpriteSheet[] spriteSheets = spriteSheetComponentStorage.getAllComponents();
        save.writeInt(spriteSheets.length);
        for(SpriteSheet spriteSheet : spriteSheets) {
            save.writeInt(spriteSheet.getId());
            spriteSheet.save(save);
        }
        final int[] spriteSheetEntities = spriteSheetComponentStorage.getAllEntities();
        for (int entity : spriteSheetEntities) {
            save.writeInt(entity);
        }
    }

    private void remapSprites(HashMap<Vector4, Vector4> remappingTable, Sprite[][] spritesToRemap) {
        for(Sprite[] sprites : spritesToRemap) {
            for(Sprite sprite : sprites) {
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
}
