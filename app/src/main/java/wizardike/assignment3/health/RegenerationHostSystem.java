package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.EntityUpdater;
import wizardike.assignment3.levels.Level;

public class RegenerationHostSystem extends RegenerationSystem {
    private static final float syncTime = 0.4f;

    private float syncCooldown = 0.0f;

    public RegenerationHostSystem() {
        super();
    }

    public RegenerationHostSystem(DataInputStream save, Deserializer deserializer) throws IOException {
        super(save, deserializer);
    }

    public void removeRegenerations(int entity, Level level) {
        final HealthSystem healthSystem = level.getHealthSystem();
        final int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
        final DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
        try {
            for(Regeneration regeneration : regenerationComponentStorage.getComponents(entity)) {
                ((HealthHost)(regeneration.health)).sync(healthSystem, levelIndex, entity, networkOut);
            }
            super.removeRegenerations(entity);
        } catch (IOException e) {
            level.getEngine().onError();
        }
    }

    public void update(Level level) {
        final Regeneration[] regenerations = regenerationComponentStorage.getAllComponents();
        final int regenerationCount = regenerationComponentStorage.size();
        final float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        for(int i = 0; i != regenerationCount; ++i) {
            regenerations[i].update(frameTime);
        }

        syncCooldown += frameTime;
        if(syncCooldown >= syncTime){
            final HealthSystem healthSystem = level.getHealthSystem();
            final int[] entities = regenerationComponentStorage.getAllEntities();
            final DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
            final int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            try {
                for(int i = 0; i != regenerationCount; ++i) {
                    ((HealthHost)(regenerations[i].health)).sync(healthSystem, levelIndex, entities[i], networkOut);
                }
            } catch (IOException e) {
                level.getEngine().onError();
            }
            syncCooldown = 0.0f;
        }
    }
}
