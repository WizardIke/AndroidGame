package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;

import static wizardike.assignment3.networking.NetworkMessageTypes.setHealth;

public class HealthHost extends Health {
    private static final int id = 1;

    static void registerLoader() {
        HealthLoader.addLoader(id, new HealthLoader.Loader() {
            @Override
            public Health load(DataInputStream save) throws IOException {
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
            final int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            sync(level.getHealthSystem(), levelIndex, target, networkOut);
        } catch (IOException e) {
            level.getEngine().onError();
        }

        return damageTaken;
    }

    void sync(HealthSystem healthSystem, int levelIndex, int thisEntity, final DataOutputStream networkOut) throws IOException {
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
