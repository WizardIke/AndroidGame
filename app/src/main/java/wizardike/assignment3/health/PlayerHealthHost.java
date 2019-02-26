package wizardike.assignment3.health;

import wizardike.assignment3.awesomeness.Awesomeness;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static wizardike.assignment3.networking.NetworkMessageTypes.setHealth;

public class PlayerHealthHost extends HealthHost {
    private static final int id = 3;

    static void registerLoader() {
        HealthComponentLoader.addLoader(id, new HealthComponentLoader.Loader() {
            @Override
            public Health load(DataInputStream save) throws IOException {
                return new PlayerHealthHost(save);
            }
        });
    }

    public PlayerHealthHost(final Resistances resistances, float armorToughness,
                            float maxHealth, float health) {
        super(resistances,armorToughness, maxHealth, health);
    }

    public PlayerHealthHost(DataInputStream saveData) throws IOException {
        super(saveData);
    }

    @Override
    public float takeDamage(Level level, int attacker, int target, float amount, Resistances.Type type) {
        if(health > 0) {
            float damageTaken = super.takeDamage(level, attacker, target, amount, type);

            final DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
            try {
                networkOut.writeInt(20);
                int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
                networkOut.writeInt(levelIndex);
                networkOut.writeInt(SystemIds.healthSystem);
                HealthSystem healthSystem = level.getHealthSystem();
                networkOut.writeInt(healthSystem.indexOf(target, this));
                networkOut.writeInt(setHealth);
                networkOut.writeFloat(health);
            } catch (IOException e) {
                level.getEngine().onError();
            }

            if (health <= 0.0f) {
                Awesomeness awesomeness = level.getAwesomenessSystem().getAwesomeness(attacker);
                if(awesomeness != null) {
                    awesomeness.increase(500);
                }
                try {
                    level.getEngine().onLoose();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return damageTaken;
        } else {
            return 0.0f;
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
