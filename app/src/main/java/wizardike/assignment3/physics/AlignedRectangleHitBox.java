package wizardike.assignment3.physics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.geometry.AlignedRectangle;
import wizardike.assignment3.levels.Level;

public class AlignedRectangleHitBox extends AlignedRectangle implements Collidable {
    private static final int id = 0;

    private float mMass;

    static void registerLoader() {
        CollidableLoader.addLoader(id, new CollidableLoader.Loader() {
            @Override
            public Collidable load(DataInputStream save) throws IOException {
                return new AlignedRectangleHitBox(save);
            }
        });
    }

    public AlignedRectangleHitBox(float x, float y, float weight, float height, float mass) {
        super(x, y, weight, height);
        mMass = mass;
    }

    private AlignedRectangleHitBox(DataInputStream save) throws IOException {
        super(save.readFloat(), save.readFloat(), save.readFloat(), save.readFloat());
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
        CollisionSystem.collide(other, this);
    }

    @Override
    public void collide(Level level, int thisEntity, TriggeredCircleHitBox other, int otherEntity) {
        CollisionSystem.collide(other, this);
    }

    public float getMass() {
        return mMass;
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeFloat(getX());
        save.writeFloat(getY());
        save.writeFloat(getWidth());
        save.writeFloat(getHeight());
        save.writeFloat(mMass);
    }

    @Override
    public int getId() {
        return id;
    }
}
