package wizardike.assignment3.animation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.movement.Movement;

public class WalkingAnimationSystem {
    private final ComponentStorage<WalkingAnimation> walkingAnimationComponentStorage;

    public WalkingAnimationSystem() {
        walkingAnimationComponentStorage = new ComponentStorage<>(WalkingAnimation.class);
    }

    public WalkingAnimationSystem(DataInputStream save, Engine engine,
                                  final EntityUpdater entityUpdater,
                                  SpriteSheet[] spriteSheetRemappingTable,
                                  Movement[] movementRemappingTable,
                                  Sprite[] spriteRemappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int walkingAnimationCount = save.readInt();
        WalkingAnimation[] walkingAnimations = new WalkingAnimation[walkingAnimationCount];
        for(int i = 0; i != walkingAnimationCount; ++i) {
            walkingAnimations[i] = new WalkingAnimation(save, spriteSheetRemappingTable,
                    movementRemappingTable, spriteRemappingTable);
        }
        int[] walkingAnimationEntities = new int[walkingAnimationCount];
        for(int i = 0; i != walkingAnimationCount; ++i) {
            final int oldEntity = save.readInt();
            walkingAnimationEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        walkingAnimationComponentStorage = new ComponentStorage<>(WalkingAnimation.class,
                walkingAnimationEntities, walkingAnimations);
    }

    public WalkingAnimation getWalkingAnimation(int entity) {
        return walkingAnimationComponentStorage.getComponent(entity);
    }

    public void addWalkingAnimation(int entity, WalkingAnimation walkingAnimation) {
        walkingAnimationComponentStorage.addComponent(entity, walkingAnimation);
    }

    public void removeWalkingAnimations(int entity) {
        walkingAnimationComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable,
                     IdentityHashMap<Movement, Integer> movementRemappingTable,
                     IdentityHashMap<Sprite, Integer> spriteRemappingTable) throws IOException {
        final WalkingAnimation[] walkingAnimations = walkingAnimationComponentStorage.getAllComponents();
        final int walkingAnimationCount = walkingAnimationComponentStorage.size();
        save.writeInt(walkingAnimationCount);
        for(int i = 0; i != walkingAnimationCount; ++i) {
            walkingAnimations[i].save(save, spriteSheetRemappingTable, movementRemappingTable,
                    spriteRemappingTable);
        }
    }

    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        WalkingAnimation[] walkingAnimations = walkingAnimationComponentStorage.getAllComponents();
        int walkingAnimationCount = walkingAnimationComponentStorage.size();
        for(int i = 0; i != walkingAnimationCount; ++i) {
            walkingAnimations[i].update(frameTime);
        }
    }
}
