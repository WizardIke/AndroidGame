package wizardike.assignment3.health;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;

public class Regeneration {
    private float amount;
    Health health;

    public Regeneration(float amount, Health health) {
        this.amount = amount;
        this.health = health;
    }

    Regeneration(DataInputStream save, Deserializer deserializer) throws IOException {
        amount = save.readFloat();
        health = deserializer.getObject(save.readInt());
    }

    void update(float frameTime) {
        health.health += amount * frameTime;
        if(health.health > health.maxHealth) {
            health.health = health.maxHealth;
        }
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeFloat(amount);
        save.writeInt(serializer.getId(health));
    }
}
