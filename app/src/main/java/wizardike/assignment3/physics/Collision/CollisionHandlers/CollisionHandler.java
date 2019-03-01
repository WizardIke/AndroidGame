package wizardike.assignment3.physics.Collision.CollisionHandlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.Collidable;

public interface CollisionHandler {
    void run(Level level, Collidable thisCollidable, int thisEntity, Collidable other, int otherEntity);
    void update(Level level, Collidable thisCollidable, int thisEntity);

    void save(DataOutputStream save, Serializer serializer) throws IOException;
    void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException;
    int getId();
}
