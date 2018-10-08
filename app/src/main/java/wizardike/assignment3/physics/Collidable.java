package wizardike.assignment3.physics;

import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.levels.Level;

public interface Collidable {
    void collide(Level level, int thisEntity, Collidable other, int otherEntity);
    void collide(Level level, int thisEntity, CircleHitBox other, int otherEntity);
    void collide(Level level, int thisEntity, AlignedRectangleHitBox other, int otherEntity);
    void collide(Level level, int thisEntity, TriggeredCircleHitBox other, int otherEntity);

    void save(DataOutputStream save) throws IOException;
    int getId();
}
