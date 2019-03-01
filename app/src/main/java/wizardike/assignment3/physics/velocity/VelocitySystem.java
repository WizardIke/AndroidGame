package wizardike.assignment3.physics.velocity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public class VelocitySystem {
    final ComponentStorage<Velocity> movementComponentStorage;

    public VelocitySystem() {
        movementComponentStorage = new ComponentStorage<>(Velocity.class);
    }

    public VelocitySystem(DataInputStream save, Deserializer deserializer) throws IOException {
        final int movementCount = save.readInt();
        Velocity[] velocities = new Velocity[movementCount];
        for(int i = 0; i != movementCount; ++i) {
            velocities[i] = new Velocity(save, deserializer);
            deserializer.addObject(velocities[i]);
        }
        int[] movementEntities = new int[movementCount];
        for(int i = 0; i != movementCount; ++i) {
            final int oldEntity = save.readInt();
            movementEntities[i] = deserializer.getEntity(oldEntity);
        }
        movementComponentStorage = new ComponentStorage<>(Velocity.class, movementEntities, velocities);
    }

    public Velocity getVelocity(int entity) {
        return movementComponentStorage.getComponent(entity);
    }

    public void addVelocity(int entity, Velocity velocity) {
        movementComponentStorage.addComponent(entity, velocity);
    }

    public void removeMovements(int entity) {
        movementComponentStorage.removeComponents(entity);
    }

    public void removeVelocity(int entity, Velocity velocity) {
        movementComponentStorage.removeComponent(entity, velocity);
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        final Velocity[] velocities = movementComponentStorage.getAllComponents();
        final int movementCount = movementComponentStorage.size();
        save.writeInt(movementCount);
        for(int i = 0; i != movementCount; ++i) {
            velocities[i].save(save, serializer);
            serializer.addObject(velocities[i]);
        }

        int[] entities = movementComponentStorage.getAllEntities();
        for (int i = 0; i != movementCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        int movementCount = movementComponentStorage.size();
        Velocity[] velocities = movementComponentStorage.getAllComponents();
        for(int i = 0; i != movementCount; ++i) {
            velocities[i].update(frameTime);
        }
    }

    public int indexOf(int entity, Velocity velocity) {
        return movementComponentStorage.indexOf(entity, velocity);
    }

    public Velocity[] getVelocities() {
        return movementComponentStorage.getAllComponents();
    }

    public IdentityHashMap<Velocity, Integer> getRemappingTable() {
        return movementComponentStorage.getRemappingTable();
    }
}
