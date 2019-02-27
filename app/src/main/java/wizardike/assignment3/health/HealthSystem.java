package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.levels.Level;

public class HealthSystem {
    private ComponentStorage<Health> healthComponentStorage;

    public HealthSystem() {
        healthComponentStorage = new ComponentStorage<>(Health.class);
    }

    public HealthSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int healthComponentCount = save.readInt();
        Health[] healths = new Health[healthComponentCount];
        for(int i = 0; i != healthComponentCount; ++i) {
            final int id = save.readInt();
            healths[i] = HealthLoader.load(id, save);
        }
        int[] healthComponentEntities = new int[healthComponentCount];
        for(int i = 0; i != healthComponentCount; ++i) {
            final int oldEntity = save.readInt();
            healthComponentEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        healthComponentStorage = new ComponentStorage<>(Health.class, healthComponentEntities, healths);
    }

    public Health getHealth(int entity) {
        return healthComponentStorage.getComponent(entity);
    }

    public void addHealth(int entity, Health health) {
        healthComponentStorage.addComponent(entity, health);
    }

    public void removeHealths(int entity) {
        healthComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save) throws IOException {
        final Health[] healths = healthComponentStorage.getAllComponents();
        final int healthComponentCount = healthComponentStorage.size();
        save.writeInt(healthComponentCount);
        for(int i = 0; i != healthComponentCount; ++i) {
            Health health = healths[i];
            save.writeInt(health.getId());
            health.save(save);
        }

        int[] entities = healthComponentStorage.getAllEntities();
        for (int i = 0; i != healthComponentCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public void handleMessage(Level level, DataInputStream networkIn) throws IOException {
        int index = networkIn.readInt();
        float health = networkIn.readFloat();
        healthComponentStorage.getAllComponents()[index]
                .setHealth(level, health, healthComponentStorage.getAllEntities()[index]);
    }

    public int indexOf(int entity, Health health) {
        return healthComponentStorage.indexOf(entity, health);
    }

    public Health[] getHealths() {
        return healthComponentStorage.getAllComponents();
    }

    public IdentityHashMap<Health, Integer> getHealthRemappingTable() {
        return healthComponentStorage.getRemappingTable();
    }
}
