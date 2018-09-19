package wizardike.assignment3.physics;

import wizardike.assignment3.Engine;

public interface CollisionHandler {
    void run(Engine world, Collidable other);
}
