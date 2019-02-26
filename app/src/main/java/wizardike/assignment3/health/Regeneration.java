package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;

public class Regeneration {
    private float amount;
    private Health health;

    public Regeneration(float amount, Health health) {
        this.amount = amount;
        this.health = health;
    }

    Regeneration(DataInputStream save, final Health[] remappingTable) throws IOException {
        amount = save.readFloat();
        health = remappingTable[save.readInt()];
    }

    void update(float frameTime) {
        health.health += amount * frameTime;
        if(health.health > health.maxHealth) {
            health.health = health.maxHealth;
        }
    }

    public void save(DataOutputStream save, final IdentityHashMap<Health, Integer> remappingTable)
            throws IOException {
        save.writeFloat(amount);
        save.writeInt(remappingTable.get(health));
    }
}
