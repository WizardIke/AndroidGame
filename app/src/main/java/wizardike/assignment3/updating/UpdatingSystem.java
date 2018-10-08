package wizardike.assignment3.updating;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entities.EntityAllocator;
import wizardike.assignment3.entities.EntityUpdater;
import wizardike.assignment3.levels.Level;

public class UpdatingSystem {
    private ComponentStorage<Updatable> updatableComponentStorage;

    public UpdatingSystem() {
        updatableComponentStorage = new ComponentStorage<>();
    }

    public UpdatingSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int updatableCount = save.readInt();
        final Updatable[] updatables = new Updatable[updatableCount];
        for(int i = 0; i != updatableCount; ++i) {
            final int id = save.readInt();
            updatables[i] = UpdatableLoader.load(id, save);
        }
        int[] updatableEntities = new int[updatableCount];
        for(int i = 0; i != updatableCount; ++i) {
            final int oldEntity = save.readInt();
            updatableEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        updatableComponentStorage = new ComponentStorage<>(updatableEntities, updatables);
    }

    public void update(Level level) {
        Updatable[] updatables = updatableComponentStorage.getAllComponents();
        for(Updatable updatable : updatables) {
            updatable.update(level);
        }
    }

    public void save(DataOutputStream save) throws IOException {
        Updatable[] updatables = updatableComponentStorage.getAllComponents();
        save.writeInt(updatables.length);
        for(Updatable updatable : updatables) {
            save.writeInt(updatable.getId());
            updatable.save(save);
        }
    }

    public void add(int entity, Updatable updatable) {
        updatableComponentStorage.addComponent(entity, updatable);
    }

    public void removeAll(int entity) {
        updatableComponentStorage.removeComponents(entity);
    }
}
