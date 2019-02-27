package wizardike.assignment3.health;

import wizardike.assignment3.awesomeness.Awesomeness;
import wizardike.assignment3.levels.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerHealthHost extends HealthHost {
    private static final int id = 4;

    static void registerLoader() {
        HealthLoader.addLoader(id, new HealthLoader.Loader() {
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
                final int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
                sync(level.getHealthSystem(), levelIndex, target, networkOut);
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
