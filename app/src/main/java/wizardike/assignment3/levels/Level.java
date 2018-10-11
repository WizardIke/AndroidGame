package wizardike.assignment3.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheetSystem;
import wizardike.assignment3.updating.UpdatingSystem;
import wizardike.assignment3.entities.EntityUpdater;
import wizardike.assignment3.graphics.GeometrySystem;
import wizardike.assignment3.graphics.LightingSystem;
import wizardike.assignment3.physics.CollisionSystem;

/**
 * A game level. All levels should be an instance of this.
 * An entity cannot have components in more than one Level.
 */
public class Level {
    public interface Callback {
        void onLoadComplete(Level level);
    }

    private UpdatingSystem updatingSystem;
    private CollisionSystem collisionSystem;
    private GeometrySystem geometrySystem;
    private LightingSystem lightingSystem;
    private SpriteSheetSystem spriteSheetSystem;

    /**
     * Makes an empty level
     */
    Level() {
        updatingSystem = new UpdatingSystem();
        collisionSystem = new CollisionSystem();
        geometrySystem = new GeometrySystem();
        lightingSystem = new LightingSystem();
        spriteSheetSystem = new SpriteSheetSystem();
    }

    /**
     * Loads a level. The level must not be used until the callback is called
     */
    Level(DataInputStream save, Engine engine, final Callback callback) throws IOException {
        final EntityUpdater entityUpdater = new EntityUpdater();
        updatingSystem = new UpdatingSystem(save, engine, entityUpdater);
        collisionSystem = new CollisionSystem(save, engine, entityUpdater);
        geometrySystem = new GeometrySystem(save, engine, entityUpdater);
        lightingSystem = new LightingSystem(save, engine, entityUpdater);
        Sprite[][] spritesToRemap = new Sprite[2][];
        spritesToRemap[0] = geometrySystem.getSprites();
        spritesToRemap[1] = geometrySystem.getTransparentSprites();
        final int[] spritesToRemapLength = new int[2];
        spritesToRemapLength[0] = geometrySystem.getSpriteCount();
        spritesToRemapLength[1] = geometrySystem.getTransparentSpriteCount();
        spriteSheetSystem = new SpriteSheetSystem(save, engine, entityUpdater, spritesToRemap,
                spritesToRemapLength, new SpriteSheetSystem.Callback() {
            @Override
            public void onLoadComplete(SpriteSheetSystem spriteSheetSystem) {
                callback.onLoadComplete(Level.this);
            }
        });
    }

    public UpdatingSystem getUpdatingSystem() {
        return updatingSystem;
    }

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    public GeometrySystem getGeometrySystem() {
        return geometrySystem;
    }

    public LightingSystem getLightingSystem() {
        return lightingSystem;
    }

    public SpriteSheetSystem getSpriteSheetSystem() {
        return spriteSheetSystem;
    }

    public void update(Engine engine) {
        updatingSystem.update(this); //update anything that needs updating
        collisionSystem.update(this); //handle collisions between objects
        geometrySystem.update(engine); //render geometry to a buffer
        lightingSystem.update(engine); //apply lighting and shadows
    }

    public void save(DataOutputStream save) throws IOException {
        updatingSystem.save(save);
        collisionSystem.save(save);
        geometrySystem.save(save);
        lightingSystem.save(save);
    }
}
