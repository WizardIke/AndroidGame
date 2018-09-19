package wizardike.assignment3.physics;

import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.Engine;

public interface Collidable {
    void collide(Engine world, Collidable other);
    void collide(Engine world, CircleHitBox other);
    void collide(Engine world, AlignedRectangleHitBox other);
    void collide(Engine world, TriggeredCircleHitBox other);
    Entity getOwningEntity();
}
