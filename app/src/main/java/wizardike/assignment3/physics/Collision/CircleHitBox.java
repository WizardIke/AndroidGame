package wizardike.assignment3.physics.Collision;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.geometry.Circle;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;

public class CircleHitBox extends Circle implements Collidable {
    private static final int id = 1;

    private float mMass;

    static void registerLoader() {
        CollidableLoader.addLoader(id, new CollidableLoader.Loader() {
            @Override
            public Collidable load(DataInputStream save, Vector2[] positionRemappingTable) throws IOException {
                return new CircleHitBox(save, positionRemappingTable);
            }
        });
    }

    public CircleHitBox(Vector2 position, float radius, float mass) {
        super(position, radius);
        mMass = mass;
    }

    private CircleHitBox(DataInputStream save, Vector2[] positionRemappingTable) throws IOException {
        super(positionRemappingTable[save.readInt()], save.readFloat());
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
    public void save(DataOutputStream save, IdentityHashMap<Vector2, Integer> positionRemappingTable) throws IOException {
        save.writeInt(positionRemappingTable.get(getPosition()));
        save.writeFloat(getRadius());
        save.writeFloat(mMass);
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {

    }

    public float getMass() {
        return mMass;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void update(Level level, int thisEntity) {

    }
}
