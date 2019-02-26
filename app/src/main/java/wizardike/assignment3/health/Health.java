package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.levels.Level;

import static java.lang.Math.pow;

/**
 * Created by Isaac on 22/08/2017.
 */
public class Health {
    private static final int id = 0;

    public Resistances resistances;
    public float armorToughness;
    public float maxHealth;
    public float health;

    static void registerLoader() {
        HealthComponentLoader.addLoader(id, new HealthComponentLoader.Loader() {
            @Override
            public Health load(DataInputStream save) throws IOException {
                return new Health(save);
            }
        });
    }


    public Health(Resistances resistances, float armorToughness, float maxHealth, float health) {
        this.resistances = resistances;
        this.armorToughness = armorToughness;
        this.maxHealth = maxHealth;
        this.health = health;
    }

    public Health(DataInputStream saveData) throws IOException {
        resistances = new Resistances(saveData);
        armorToughness = saveData.readFloat();
        maxHealth = saveData.readFloat();
        health = saveData.readFloat();
    }

    public float takeDamage(Level level, int attacker, int target, float amount, Resistances.Type type) {
        final float resistance = resistances.resistances[type.value];
        final float damageTaken = amount - resistance * (float)pow((1.0f / resistance * amount), armorToughness);
        health -= damageTaken;
        return damageTaken;
    }

    public void save(DataOutputStream saveData) throws IOException {
        resistances.save(saveData);
        saveData.writeFloat(armorToughness);
        saveData.writeFloat(maxHealth);
        saveData.writeFloat(health);
    }

    public int getId() {
        return id;
    }

    public void setHealth(Level level, float health, int thisEntity) {
        this.health = health;
    }
}
