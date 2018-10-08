package wizardike.assignment3.physics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.geometry.Circle;
import wizardike.assignment3.levels.Level;

public class CircleHitBox extends Circle implements Collidable {
    private static final int id = 1;

    private float mMass;

    static void registerLoader() {
        CollidableLoader.addLoader(id, new CollidableLoader.Loader() {
            @Override
            public Collidable load(DataInputStream save) throws IOException {
                return new CircleHitBox(save);
            }
        });
    }

    public CircleHitBox(float x, float y, float radius, float mass) {
        super(x, y, radius);
        mMass = mass;
    }

    private CircleHitBox(DataInputStream save) throws IOException {
        super(save.readFloat(), save.readFloat(), save.readFloat());
        mMass = save.readFloat();
    }

    @Override
    public void collide(Level level, int thisEntity, Collidable other, int otherEntity) {
        other.collide(level, thisEntity, this, otherEntity);
    }

    @Override
    public void collide(Level level, int thisEntity, CircleHitBox other, int otherEntity) {
        CollisionSystem.collide(other, this);
    }

    @Override
    public void collide(Level level, int thisEntity, AlignedRectangleHitBox other, int otherEntity) {
        CollisionSystem.collide(this, other);
    }

    @Override
    public void collide(Level level, int thisEntity, TriggeredCircleHitBox other, int otherEntity) {
        CollisionSystem.collide(level, other, otherEntity, this, thisEntity);
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeFloat(getX());
        save.writeFloat(getY());
        save.writeFloat(getRadius());
        save.writeFloat(mMass);
    }

    public float getMass() {
        return mMass;
    }

    @Override
    public int getId() {
        return id;
    }
}
