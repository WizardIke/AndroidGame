package wizardike.assignment3.updating;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

public class UpdatingSystem {
    private ComponentStorage<Updatable> updatableComponentStorage;

    public UpdatingSystem() {
        updatableComponentStorage = new ComponentStorage<>(Updatable.class);
    }

    public UpdatingSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater,
                          SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int updatableCount = save.readInt();
        final Updatable[] updatables = new Updatable[updatableCount];
        for(int i = 0; i != updatableCount; ++i) {
            final int id = save.readInt();
            updatables[i] = UpdatableLoader.load(id, save, spriteSheetRemappingTable);
        }
        int[] updatableEntities = new int[updatableCount];
        for(int i = 0; i != updatableCount; ++i) {
            final int oldEntity = save.readInt();
            updatableEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        updatableComponentStorage = new ComponentStorage<>(Updatable.class, updatableEntities, updatables);
    }

    public void update(Level level) {
        final Updatable[] updatables = updatableComponentStorage.getAllComponents();
        final int[] entities = updatableComponentStorage.getAllEntities();
        final int updatableCount = updatableComponentStorage.size();
        for(int i = 0; i != updatableCount; ++i) {
            updatables[i].update(level, entities[i]);
        }
    }

    public void save(DataOutputStream save, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException {
        final Updatable[] updatables = updatableComponentStorage.getAllComponents();
        final int updatableCount = updatableComponentStorage.size();
        save.writeInt(updatableCount);
        for(int i = 0; i != updatableCount; ++i) {
            Updatable updatable = updatables[i];
            save.writeInt(updatable.getId());
            updatable.save(save, spriteSheetRemappingTable);
        }

        int[] entities = updatableComponentStorage.getAllEntities();
        for (int i = 0; i != updatableCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public void add(int entity, Updatable updatable) {
        updatableComponentStorage.addComponent(entity, updatable);
    }

    public void removeAll(int entity) {
        updatableComponentStorage.removeComponents(entity);
    }

    public void remove(int entity, Updatable updatable) {
        updatableComponentStorage.removeComponent(entity, updatable);
    }

    public int indexOf(int entity, Updatable updatable) {
        return updatableComponentStorage.indexOf(entity, updatable);
    }
}
