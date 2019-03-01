package wizardike.assignment3.userInterface;

import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public interface AnalogStickListener {
    void start(int entity, Level level, float directionX, float directionY);
    void move(int entity, Level level, float directionX, float directionY);
    void stop(int entity, Level level, float directionX, float directionY);
    int getId();
    void save(DataOutputStream save, int entity, Level level, Serializer serializer) throws IOException;
}
