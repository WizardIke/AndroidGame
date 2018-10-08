package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entities.EntityUpdater;

/**
 * Handles saving and loading sprite sheets
 */
public class SpriteSheetSystem {
    private ComponentStorage<SpriteSheet> spriteSheetComponentStorage;

    public SpriteSheetSystem() {
        spriteSheetComponentStorage = new ComponentStorage<>();
    }

    public SpriteSheetSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater,
                             Sprite[][] spritesToRemap) throws IOException {
        //TODO load sprite sheets and create remapping table
        for(Sprite[] sprites : spritesToRemap) {
            for(Sprite sprite : sprites) {
                //TODO remap sprite
            }
        }
    }

    public void addSpriteSheet(int entity, SpriteSheet spriteSheet) {
        spriteSheetComponentStorage.addComponent(entity, spriteSheet);
    }

    public void removeSpriteSheets(int entity) {
        spriteSheetComponentStorage.removeComponents(entity);
    }

    /**
     * Should be called when the textureManager is recreated
     */
    public void recreateAll(Sprite[][] spritesToRemap) {
        //TODO recreate sprite sheets and create remapping table
        for(Sprite[] sprites : spritesToRemap) {
            for(Sprite sprite : sprites) {
                //TODO remap sprite
            }
        }
    }

    public void save(DataOutputStream save) throws IOException {

    }
}
