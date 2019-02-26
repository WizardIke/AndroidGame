package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;

import static wizardike.assignment3.networking.NetworkMessageTypes.setHealth;

public class HealthHost extends Health {
    private static final int id = 0;

    static void registerLoader() {
        HealthHostLoader.addLoader(id, new HealthHostLoader.Loader() {
            @Override
            public HealthHost load(DataInputStream save) throws IOException {
                return new HealthHost(save);
            }
        });
    }

    public HealthHost(Resistances resistances, float armorToughness, float maxHealth, float health) {
        super(resistances, armorToughness, maxHealth, health);
    }

    public HealthHost(DataInputStream saveData) throws IOException {
        super(saveData);
    }

    @Override
    public float takeDamage(Level level, int attacker, int target, float amount, Resistances.Type type) {
        float damageTaken = super.takeDamage(level, attacker, target, amount, type);
        final DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
        try {
            networkOut.writeInt(20);
            int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            networkOut.writeInt(levelIndex);
            networkOut.writeInt(SystemIds.healthSystem);
            HealthHostSystem healthSystem = level.getHealthHostSystem();
            networkOut.writeInt(healthSystem.indexOf(target, this));
            networkOut.writeInt(setHealth);
            networkOut.writeFloat(health);
        } catch (IOException e) {
            level.getEngine().onError();
        }

        return damageTaken;
    }

    public void sync(HealthHostSystem healthSystem, int levelIndex, int thisEntity, final DataOutputStream networkOut) throws IOException {
        networkOut.writeInt(16);
        networkOut.writeInt(levelIndex);
        networkOut.writeInt(SystemIds.healthSystem);
        networkOut.writeInt(healthSystem.indexOf(thisEntity, this));
        networkOut.writeFloat(health);
    }

    public int getId() {
        return id;
    }
}
