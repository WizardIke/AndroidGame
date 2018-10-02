package wizardike.assignment3.physics;

import wizardike.assignment3.geometry.AlignedRectangle;
import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.Engine;

public class AlignedRectangleHitBox extends AlignedRectangle implements Collidable {
    private Entity mOwner;
    private float mMass;

    public AlignedRectangleHitBox(float x, float y, float weight, float height, float mass, Entity owner) {
        super(x, y, weight, height);
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
        CollisionSystem.collide(world, other, this);
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
