package wizardike.assignment3.physics.Collision;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;

public interface Collidable {
    void collide(Level level, int thisEntity, Collidable other, int otherEntity);
    void collide(Level level, int thisEntity, CircleHitBox other, int otherEntity);
    void collide(Level level, int thisEntity, AlignedRectangleHitBox other, int otherEntity);
    void collide(Level level, int thisEntity, TriggeredCircleHitBox other, int otherEntity);

    void update(Level level, int thisEntity);
    void save(DataOutputStream save, Serializer serializer) throws IOException;
    void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException;
    int getId();
}
