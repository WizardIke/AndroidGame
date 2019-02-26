package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.awesomeness.Awesomeness;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;

import static wizardike.assignment3.networking.NetworkMessageTypes.setHealth;

public class SkeletonHealthHost extends HealthHost {
    private static final int id = 1;

    private static final float armorToughness = 0.1f;
    private static final float startingFireResistance = 0.2f;
    private static final float startingColdResistance = 1.25f;
    private static final float startingLightningResistance = 0.3f;
    private static final float startingArcaneResistance = 0.2f;
    private static final float startingBludgeoningResistance = 0.1f;
    private static final float startingPiecingResistance = 1.4f;
    private static final float startingSlashingResistance = 0.2f;

    private static final int awesomenessForKill = 1;

    static void registerLoader() {
        HealthHostLoader.addLoader(id, new HealthHostLoader.Loader() {
            @Override
            public HealthHost load(DataInputStream save) throws IOException {
                return new SkeletonHealthHost(save);
            }
        });
    }

    public SkeletonHealthHost(float maxHealth, float health) {
        super(new Resistances(startingFireResistance, startingColdResistance, startingLightningResistance,
                        startingArcaneResistance, startingBludgeoningResistance, startingPiecingResistance, startingSlashingResistance),
                armorToughness, maxHealth, health);
    }

    public SkeletonHealthHost(DataInputStream saveData) throws IOException {
        super(saveData);
    }

    @Override
    public float takeDamage(final Level level, final int attacker, final int target, final float amount,
                           final Resistances.Type type) {
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
            if (health <= 0.0) {
                level.getDestructionSystem().delayedDestroy(target);
                Awesomeness awesomeness = level.getAwesomenessSystem().getAwesomeness(attacker);
                awesomeness.increase(awesomenessForKill);
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
