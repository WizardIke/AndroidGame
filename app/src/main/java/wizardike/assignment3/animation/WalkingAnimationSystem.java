package wizardike.assignment3.animation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public class WalkingAnimationSystem {
    private final ComponentStorage<WalkingAnimation> walkingAnimationComponentStorage;

    public WalkingAnimationSystem() {
        walkingAnimationComponentStorage = new ComponentStorage<>(WalkingAnimation.class);
    }

    public WalkingAnimationSystem(DataInputStream save, Deserializer deserializer) throws IOException {

        final int walkingAnimationCount = save.readInt();
        WalkingAnimation[] walkingAnimations = new WalkingAnimation[walkingAnimationCount];
        for(int i = 0; i != walkingAnimationCount; ++i) {
            walkingAnimations[i] = new WalkingAnimation(save, deserializer);
            deserializer.addObject(walkingAnimations[i]);
        }
        int[] walkingAnimationEntities = new int[walkingAnimationCount];
        for(int i = 0; i != walkingAnimationCount; ++i) {
            final int oldEntity = save.readInt();
            walkingAnimationEntities[i] = deserializer.getEntity(oldEntity);
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

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        final WalkingAnimation[] walkingAnimations = walkingAnimationComponentStorage.getAllComponents();
        final int walkingAnimationCount = walkingAnimationComponentStorage.size();
        save.writeInt(walkingAnimationCount);
        for(int i = 0; i != walkingAnimationCount; ++i) {
            walkingAnimations[i].save(save, serializer);
            serializer.addObject(walkingAnimations[i]);
        }

        final int[] entities = walkingAnimationComponentStorage.getAllEntities();
        for (int i = 0; i != walkingAnimationCount; ++i) {
            save.writeInt(entities[i]);
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
