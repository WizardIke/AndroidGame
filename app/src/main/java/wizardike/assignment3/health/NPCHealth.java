package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.awesomeness.Awesomeness;
import wizardike.assignment3.levels.Level;

/**
 * Created by Isaac on 29/08/2017.
 */
public class NPCHealth extends Health {
    private static final int id = 1;

    static void registerLoader() {
        HealthComponentLoader.addLoader(id, new HealthComponentLoader.Loader() {
            @Override
            public Health load(DataInputStream save) throws IOException {
                return new NPCHealth(save);
            }
        });
    }

    public NPCHealth(final Resistances resistances, float armorToughness,
                     float maxHealth, float health) {
        super(resistances, armorToughness, maxHealth, health);
    }

    private NPCHealth(DataInputStream saveData) throws IOException {
        super(saveData);
    }

    @Override
    public float takeDamage(Level level, int attacker, int target, float amount, Resistances.Type type) {
        if(health > 0) {
            float damageTaken = super.takeDamage(level, attacker, target, amount, type);
            if (health <= 0.0) {
                Awesomeness awesomeness = level.getAwesomenessSystem().getAwesomeness(attacker);
                awesomeness.increase(500);
                try {
                    level.getEngine().onWin();
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

    @Override
    public void setHealth(Level level, float health, int thisEntity) {
        super.setHealth(level, health, thisEntity);
        if (health <= 0.0) {
            try {
                level.getEngine().onWin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
