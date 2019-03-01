package wizardike.assignment3.talents.primary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public interface PrimaryTalent {
    boolean isReady();
    void activate(Level level, int entity, float directionX, float directionY);
    void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException;
    void update(Level level);
    void save(DataOutputStream save, Serializer serializer) throws IOException;
    int getId();
    float getCooldown();
    void setCooldown(float cooldown);
    float getRange();
}
