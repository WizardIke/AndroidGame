package wizardike.assignment3.physics.movement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;

public class MovementSystem {
    private final ComponentStorage<Movement> movementComponentStorage;

    public MovementSystem() {
        movementComponentStorage = new ComponentStorage<>(Movement.class);
    }

    public MovementSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater, final Vector2[] remappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int movementCount = save.readInt();
        Movement[] movements = new Movement[movementCount];
        for(int i = 0; i != movementCount; ++i) {
            movements[i] = new Movement(save, remappingTable);
        }
        int[] movementEntities = new int[movementCount];
        for(int i = 0; i != movementCount; ++i) {
            final int oldEntity = save.readInt();
            movementEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        movementComponentStorage = new ComponentStorage<>(Movement.class, movementEntities, movements);
    }

    public Movement getMovement(int entity) {
        return movementComponentStorage.getComponent(entity);
    }

    public void addMovement(int entity, Movement movement) {
        movementComponentStorage.addComponent(entity, movement);
    }

    public void removeMovements(int entity) {
        movementComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, final IdentityHashMap<Vector2, Integer> remappingTable) throws IOException {
        final Movement[] movements = movementComponentStorage.getAllComponents();
        final int movementCount = movementComponentStorage.size();
        save.writeInt(movementCount);
        for(int i = 0; i != movementCount; ++i) {
            movements[i].save(save, remappingTable);
        }

        int[] entities = movementComponentStorage.getAllEntities();
        for (int i = 0; i != movementCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        int movementCount = movementComponentStorage.size();
        Movement[] movements = movementComponentStorage.getAllComponents();
        for(int i = 0; i != movementCount; ++i) {
            movements[i].update(frameTime);
        }
    }

    public Movement[] getMovements() {
        return movementComponentStorage.getAllComponents();
    }

    public IdentityHashMap<Movement, Integer> getRemappingTable() {
        return movementComponentStorage.getRemappingTable();
    }
}
