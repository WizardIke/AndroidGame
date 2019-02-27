package wizardike.assignment3.physics.movement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;
import wizardike.assignment3.position.PositionSystem;

public class MovementHostSystem extends MovementSystem {
    private static final float syncTime = 0.4f;
    private float syncCooldown = 0.0f;

    public MovementHostSystem() {
        super();
    }

    public MovementHostSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater, final Vector2[] remappingTable) throws IOException {
        super(save, engine, entityUpdater, remappingTable);
    }

    /**
     * Removes the movement components and notifies the client
     * @param entity The entity that the component belongs to.
     * @param level The current level
     */
    public void removeMovements(int entity, Level level) {
        final int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
        final DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
        final PositionSystem positionSystem = level.getPositionSystem();
        try {
            for(Movement movement : movementComponentStorage.getComponents(entity)) {
                syncPosition(networkOut, levelIndex, positionSystem, entity, movement.position);
            }
            super.removeMovements(entity);
        } catch (IOException e) {
            level.getEngine().onError();
        }
    }

    public void update(Level level) {
        super.update(level);

        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        syncCooldown += frameTime;
        if(syncCooldown >= syncTime){
            Movement[] movements = getMovements();
            int[] entities = movementComponentStorage.getAllEntities();
            DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
            final PositionSystem positionSystem = level.getPositionSystem();
            int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            final int positionCount = movementComponentStorage.size();
            try {
                for(int i = 0; i != positionCount; ++i) {
                    syncPosition(networkOut, levelIndex, positionSystem, entities[i], movements[i].position);
                }
            } catch (IOException e) {
                level.getEngine().onError();
            }
            syncCooldown = 0.0f;
        }
    }

    private void syncPosition(final DataOutputStream networkOut, final int levelIndex, PositionSystem positionSystem,
                              int entity, Vector2 position) throws IOException {
        networkOut.writeInt(20);
        networkOut.writeInt(levelIndex);
        networkOut.writeInt(SystemIds.positionSystem);
        networkOut.writeInt(positionSystem.indexOf(entity, position));
        networkOut.writeFloat(position.getX());
        networkOut.writeFloat(position.getY());
    }
}
