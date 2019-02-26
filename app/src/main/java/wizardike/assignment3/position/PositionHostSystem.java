package wizardike.assignment3.position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;

public class PositionHostSystem extends PositionSystem {
    private static final float syncTime = 0.2f;
    private float syncCooldown = 0.0f;

    public PositionHostSystem() {
        super();
    }

    public PositionHostSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        super(save, engine, entityUpdater);
    }

    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        syncCooldown += frameTime;
        if(syncCooldown >= syncTime){
            Vector2[] positions = getPositions();
            int[] positionEntities = getEntities();
            final int positionCount = getPositionCount();
            DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
            int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            try {
                for(int i = 0; i != positionCount; ++i) {
                    networkOut.writeInt(20);
                    networkOut.writeInt(levelIndex);
                    networkOut.writeInt(SystemIds.positionSystem);
                    networkOut.writeInt(super.indexOf(positionEntities[i], positions[i]));
                    networkOut.writeFloat(positions[i].getX());
                    networkOut.writeFloat(positions[i].getY());
                }
            } catch (IOException e) {
                level.getEngine().onError();
            }
            syncCooldown = 0.0f;
        }
    }
}
