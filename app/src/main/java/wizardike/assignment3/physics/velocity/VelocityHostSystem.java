package wizardike.assignment3.physics.velocity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;
import wizardike.assignment3.position.PositionSystem;

public class VelocityHostSystem extends VelocitySystem {
    private static final float syncTime = 0.4f;
    private float syncCooldown = 0.0f;

    public VelocityHostSystem() {
        super();
    }

    public VelocityHostSystem(DataInputStream save, Deserializer deserializer) throws IOException {
        super(save, deserializer);
    }

    /**
     * Removes the velocity components and notifies the client
     * @param entity The entity that the component belongs to.
     * @param level The current level
     */
    public void removeVelocities(int entity, Level level) {
        final int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
        final DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
        final PositionSystem positionSystem = level.getPositionSystem();
        try {
            for(Velocity velocity : movementComponentStorage.getComponents(entity)) {
                syncPosition(networkOut, levelIndex, positionSystem, entity, velocity.position);
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
            Velocity[] velocities = getVelocities();
            int[] entities = movementComponentStorage.getAllEntities();
            DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
            final PositionSystem positionSystem = level.getPositionSystem();
            int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            final int positionCount = movementComponentStorage.size();
            try {
                for(int i = 0; i != positionCount; ++i) {
                    syncPosition(networkOut, levelIndex, positionSystem, entities[i], velocities[i].position);
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
