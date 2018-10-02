package wizardike.assignment3.physics;

import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.Engine;

public class TriggeredCircleHitBox extends CircleHitBox implements Collidable {
    private CollisionHandler mEffect;

    public TriggeredCircleHitBox(float x, float y, float radius, float mass, CollisionHandler effect, Entity owner) {
        super(x, y, radius, mass, owner);
        mEffect = effect;
    }

    @Override
    public void collide(Engine world, Collidable other) {
        other.collide(world, this);
    }

    @Override
    public void collide(Engine world, CircleHitBox other) {
        CollisionSystem.collide(world, other, this);
    }

    @Override
    public void collide(Engine world, AlignedRectangleHitBox other) {
        CollisionSystem.collide(world, this, other);
    }

    @Override
    public void collide(Engine world, TriggeredCircleHitBox other) {
        CollisionSystem.collide(world, other, this);
    }

    public void onCollision(Engine world, Collidable other) {
        mEffect.run(world, other);
    }
}
