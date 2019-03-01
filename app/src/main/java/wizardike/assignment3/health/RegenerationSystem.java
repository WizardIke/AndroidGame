package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public class RegenerationSystem {
    final ComponentStorage<Regeneration> regenerationComponentStorage;

    public RegenerationSystem() {
        regenerationComponentStorage = new ComponentStorage<>(Regeneration.class);
    }

    public RegenerationSystem(DataInputStream save, Deserializer deserializer) throws IOException {
        final int regenerationCount = save.readInt();
        Regeneration[] regenerations = new Regeneration[regenerationCount];
        for(int i = 0; i != regenerationCount; ++i) {
            regenerations[i] = new Regeneration(save, deserializer);
            deserializer.addObject(regenerations[i]);
        }
        int[] regenerationEntities = new int[regenerationCount];
        for(int i = 0; i != regenerationCount; ++i) {
            final int oldEntity = save.readInt();
            regenerationEntities[i] = deserializer.getEntity(oldEntity);
        }
        regenerationComponentStorage = new ComponentStorage<>(Regeneration.class,
                regenerationEntities, regenerations);
    }

    public Regeneration getRegeneration(int entity) {
        return regenerationComponentStorage.getComponent(entity);
    }

    public void addRegeneration(int entity, Regeneration regeneration) {
        regenerationComponentStorage.addComponent(entity, regeneration);
    }

    public void removeRegenerations(int entity) {
        regenerationComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        final Regeneration[] regenerations = regenerationComponentStorage.getAllComponents();
        final int regenerationCount = regenerationComponentStorage.size();
        save.writeInt(regenerationCount);
        for(int i = 0; i != regenerationCount; ++i) {
            regenerations[i].save(save, serializer);
            serializer.addObject(regenerations[i]);
        }

        int[] entities = regenerationComponentStorage.getAllEntities();
        for (int i = 0; i != regenerationCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public void update(Level level) {
        final Regeneration[] regenerations = regenerationComponentStorage.getAllComponents();
        final int regenerationCount = regenerationComponentStorage.size();
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        for(int i = 0; i != regenerationCount; ++i) {
            regenerations[i].update(frameTime);
        }
    }
}
