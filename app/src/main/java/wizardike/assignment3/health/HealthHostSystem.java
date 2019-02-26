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

import static wizardike.assignment3.networking.NetworkMessageTypes.setHealth;

public class HealthHostSystem {
    private static final float syncTime = 0.4f;

    private float syncCooldown = 0.0f;
    private ComponentStorage<HealthHost> healthComponentStorage;

    public HealthHostSystem() {
        healthComponentStorage = new ComponentStorage<>(HealthHost.class);
    }

    public HealthHostSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int healthComponentCount = save.readInt();
        HealthHost[] healths = new HealthHost[healthComponentCount];
        for(int i = 0; i != healthComponentCount; ++i) {
            final int id = save.readInt();
            healths[i] = HealthHostLoader.load(id, save);
        }
        int[] healthComponentEntities = new int[healthComponentCount];
        for(int i = 0; i != healthComponentCount; ++i) {
            final int oldEntity = save.readInt();
            healthComponentEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        healthComponentStorage = new ComponentStorage<>(HealthHost.class, healthComponentEntities, healths);
    }

    public Health getHealth(int entity) {
        return healthComponentStorage.getComponent(entity);
    }

    public void addHealth(int entity, HealthHost health) {
        healthComponentStorage.addComponent(entity, health);
    }

    public void removeHealths(int entity) {
        healthComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save) throws IOException {
        final HealthHost[] healths = healthComponentStorage.getAllComponents();
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
        int message = networkIn.readInt();
        switch(message) {
            case setHealth: {
                float health = networkIn.readFloat();
                healthComponentStorage.getAllComponents()[index]
                        .setHealth(level, health, healthComponentStorage.getAllEntities()[index]);
            }
        }
    }

    public int indexOf(int entity, HealthHost health) {
        return healthComponentStorage.indexOf(entity, health);
    }

    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        syncCooldown += frameTime;
        if(syncCooldown >= syncTime){
            HealthHost[] healthHosts = healthComponentStorage.getAllComponents();
            int[] healthHostEntities = healthComponentStorage.getAllEntities();
            final int healthCount = healthComponentStorage.size();
            DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
            int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            try {
                for(int i = 0; i != healthCount; ++i) {
                    healthHosts[i].sync(this, levelIndex, healthHostEntities[i], networkOut);
                }
            } catch (IOException e) {
                level.getEngine().onError();
            }
            syncCooldown = 0.0f;
        }
    }

    public HealthHost[] getHealths() {
        return healthComponentStorage.getAllComponents();
    }

    public IdentityHashMap<Health, Integer> getHealthRemappingTable() {
        IdentityHashMap<Health, Integer> remappingTable = new IdentityHashMap<>();
        final HealthHost[] components = healthComponentStorage.getAllComponents();
        final int positionCount = healthComponentStorage.size();
        for(int i = 0; i != positionCount; ++i) {
            remappingTable.put(components[i], i);
        }
        return remappingTable;
    }
}
