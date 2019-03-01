package wizardike.assignment3.animation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public class FireBoltAnimationSystem {
    private final ComponentStorage<FireBoltAnimation> fireBoltAnimationComponentStorage;

    public FireBoltAnimationSystem() {
        fireBoltAnimationComponentStorage = new ComponentStorage<>(FireBoltAnimation.class);
    }

    public FireBoltAnimationSystem(DataInputStream save, Deserializer deserializer) throws IOException {
        final int fireBoltAnimationCount = save.readInt();
        FireBoltAnimation[] fireBoltAnimations = new FireBoltAnimation[fireBoltAnimationCount];
        for(int i = 0; i != fireBoltAnimationCount; ++i) {
            fireBoltAnimations[i] = new FireBoltAnimation(save, deserializer);
            deserializer.addObject(fireBoltAnimations[i]);
        }
        int[] fireBoltAnimationEntities = new int[fireBoltAnimationCount];
        for(int i = 0; i != fireBoltAnimationCount; ++i) {
            final int oldEntity = save.readInt();
            fireBoltAnimationEntities[i] = deserializer.getEntity(oldEntity);
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

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        final FireBoltAnimation[] fireBoltAnimations = fireBoltAnimationComponentStorage.getAllComponents();
        final int fireBoltAnimationCount = fireBoltAnimationComponentStorage.size();
        save.writeInt(fireBoltAnimationCount);
        for(int i = 0; i != fireBoltAnimationCount; ++i) {
            fireBoltAnimations[i].save(save, serializer);
            serializer.addObject(fireBoltAnimations[i]);
        }

        final int[] entities = fireBoltAnimationComponentStorage.getAllEntities();
        for (int i = 0; i != fireBoltAnimationCount; ++i) {
            save.writeInt(entities[i]);
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
