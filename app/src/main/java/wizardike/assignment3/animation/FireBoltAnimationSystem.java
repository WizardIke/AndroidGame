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

public class FireBoltAnimationSystem {
    private final ComponentStorage<FireBoltAnimation> fireBoltAnimationComponentStorage;

    public FireBoltAnimationSystem() {
        fireBoltAnimationComponentStorage = new ComponentStorage<>(FireBoltAnimation.class);
    }

    public FireBoltAnimationSystem(DataInputStream save, Engine engine,
                                  final EntityUpdater entityUpdater,
                                  SpriteSheet[] spriteSheetRemappingTable,
                                  Sprite[] spriteRemappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int fireBoltAnimationCount = save.readInt();
        FireBoltAnimation[] fireBoltAnimations = new FireBoltAnimation[fireBoltAnimationCount];
        for(int i = 0; i != fireBoltAnimationCount; ++i) {
            fireBoltAnimations[i] = new FireBoltAnimation(save, spriteSheetRemappingTable,
                    spriteRemappingTable);
        }
        int[] fireBoltAnimationEntities = new int[fireBoltAnimationCount];
        for(int i = 0; i != fireBoltAnimationCount; ++i) {
            final int oldEntity = save.readInt();
            fireBoltAnimationEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        fireBoltAnimationComponentStorage = new ComponentStorage<>(FireBoltAnimation.class,
                fireBoltAnimationEntities, fireBoltAnimations);
    }

    public FireBoltAnimation getFireBoltAnimation(int entity) {
        return fireBoltAnimationComponentStorage.getComponent(entity);
    }

    public void addFireBoltAnimation(int entity, FireBoltAnimation fireBoltAnimation) {
        fireBoltAnimationComponentStorage.addComponent(entity, fireBoltAnimation);
    }

    public void removeFireBoltAnimations(int entity) {
        fireBoltAnimationComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save,
                     IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable,
                     IdentityHashMap<Sprite, Integer> spriteRemappingTable) throws IOException {
        final FireBoltAnimation[] walkingAnimations = fireBoltAnimationComponentStorage.getAllComponents();
        final int fireBoltAnimationCount = fireBoltAnimationComponentStorage.size();
        save.writeInt(fireBoltAnimationCount);
        for(int i = 0; i != fireBoltAnimationCount; ++i) {
            walkingAnimations[i].save(save, spriteSheetRemappingTable, spriteRemappingTable);
        }
    }

    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        FireBoltAnimation[] fireBoltAnimations = fireBoltAnimationComponentStorage.getAllComponents();
        int walkingAnimationCount = fireBoltAnimationComponentStorage.size();
        for(int i = 0; i != walkingAnimationCount; ++i) {
            fireBoltAnimations[i].update(frameTime);
        }
    }
}
