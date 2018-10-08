package wizardike.assignment3.physics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.levels.Level;

public class TriggeredCircleHitBox extends CircleHitBox implements Collidable {
    private static final int id = 2;

    private CollisionHandler handler;

    static void registerLoader() {
        CollidableLoader.addLoader(id, new CollidableLoader.Loader() {
            @Override
            public Collidable load(DataInputStream save) throws IOException {
                return new TriggeredCircleHitBox(save);
            }
        });
    }

    public TriggeredCircleHitBox(float x, float y, float radius, float mass, CollisionHandler handler) {
        super(x, y, radius, mass);
        this.handler = handler;
    }

    private TriggeredCircleHitBox(DataInputStream save) throws IOException {
        super(save.readFloat(), save.readFloat(), save.readFloat(), save.readFloat());

        final int id = save.readInt();
        handler = CollisionHandlerLoader.load(id, save);
    }

    @Override
    public void collide(Level level, int thisEntity, Collidable other, int otherEntity) {
        other.collide(level, thisEntity, this, otherEntity);
    }

    @Override
    public void collide(Level level, int thisEntity, CircleHitBox other, int otherEntity) {
        CollisionSystem.collide(level, this, thisEntity, other, otherEntity);
    }

    @Override
    public void collide(Level level, int thisEntity, AlignedRectangleHitBox other, int otherEntity) {
        CollisionSystem.collide(level, this, thisEntity, other, otherEntity);
    }

    @Override
    public void collide(Level level, int thisEntity, TriggeredCircleHitBox other, int otherEntity) {
        CollisionSystem.collide(level, this, thisEntity, other, otherEntity);
    }

    public void onCollision(Level level, int thisEntity, Collidable other, int otherEntity) {
        handler.run(level, thisEntity, other, otherEntity);
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        super.save(save);
        save.writeInt(handler.getId());
        handler.save(save);
    }

    @Override
    public int getId() {
        return id;
    }
}
