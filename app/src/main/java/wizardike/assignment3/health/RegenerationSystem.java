package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.levels.Level;

public class RegenerationSystem {
    private final ComponentStorage<Regeneration> regenerationComponentStorage;

    public RegenerationSystem() {
        regenerationComponentStorage = new ComponentStorage<>(Regeneration.class);
    }

    public RegenerationSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater,
                              final Health[] remappingTable)
            throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int regenerationCount = save.readInt();
        Regeneration[] regenerations = new Regeneration[regenerationCount];
        for(int i = 0; i != regenerationCount; ++i) {
            regenerations[i] = new Regeneration(save, remappingTable);
        }
        int[] regenerationEntities = new int[regenerationCount];
        for(int i = 0; i != regenerationCount; ++i) {
            final int oldEntity = save.readInt();
            regenerationEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
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

    public void save(DataOutputStream save, final IdentityHashMap<Health, Integer> remappingTable) throws IOException {
        final Regeneration[] regenerations = regenerationComponentStorage.getAllComponents();
        final int regenerationCount = regenerationComponentStorage.size();
        save.writeInt(regenerationCount);
        for(int i = 0; i != regenerationCount; ++i) {
            regenerations[i].save(save, remappingTable);
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
