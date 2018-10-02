package wizardike.assignment3.physics;

import wizardike.assignment3.geometry.Circle;
import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.Engine;

public class CircleHitBox extends Circle implements Collidable {
    private Entity mOwner;
    private float mMass;

    public CircleHitBox(float x, float y, float radius, float mass, Entity owner) {
        super(x, y, radius);
        mOwner = owner;
        mMass = mass;
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

    @Override
    public Entity getOwningEntity() {
        return mOwner;
    }

    public float getMass() {
        return mMass;
    }
}
