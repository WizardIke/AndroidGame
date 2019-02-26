package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.awesomeness.Awesomeness;
import wizardike.assignment3.levels.Level;

/**
 * Created by Isaac on 28/08/2017.
 */
public class SkeletonHealth extends Health {
    private static final float armorToughness = 0.1f;
    private static final float startingFireResistance = 0.2f;
    private static final float startingColdResistance = 1.25f;
    private static final float startingLightningResistance = 0.3f;
    private static final float startingArcaneResistance = 0.2f;
    private static final float startingBludgeoningResistance = 0.1f;
    private static final float startingPiecingResistance = 1.4f;
    private static final float startingSlashingResistance = 0.2f;

    private static final int awesomenessForKill = 1;


    public SkeletonHealth(float maxHealth, float health) {
        super(new Resistances(startingFireResistance, startingColdResistance, startingLightningResistance,
                        startingArcaneResistance, startingBludgeoningResistance, startingPiecingResistance, startingSlashingResistance),
                armorToughness, maxHealth, health);
    }

    public SkeletonHealth(DataInputStream saveData) throws IOException {
        super(saveData);
    }

    public float takeDamage(Level level, int attacker, int target, float amount, Resistances.Type type) {
        if(health > 0) {
            float damageTaken = super.takeDamage(level, attacker, target, amount, type);

            if (health <= 0.0f) {
                level.getDestructionSystem().delayedDestroy(target);
                Awesomeness awesomeness = level.getAwesomenessSystem().getAwesomeness(attacker);
                if(awesomeness != null) {
                    awesomeness.increase(awesomenessForKill);
                }
            }
            return damageTaken;
        } else {
            return 0.0f;
        }
    }

    @Override
    public void setHealth(Level level, float health, int thisEntity) {
        super.setHealth(level, health, thisEntity);
        if (health <= 0.0) {
            level.getDestructionSystem().delayedDestroy(thisEntity);
        }
    }
}
