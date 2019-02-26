package wizardike.assignment3.physics.Collision;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CollisionHandlers.CollisionHandler;
import wizardike.assignment3.physics.Collision.CollisionHandlers.CollisionHandlerLoader;

public class TriggeredCircleHitBox extends CircleHitBox implements Collidable {
    private static final int id = 2;

    private CollisionHandler handler;

    static void registerLoader() {
        CollidableLoader.addLoader(id, new CollidableLoader.Loader() {
            @Override
            public Collidable load(DataInputStream save, Vector2[] positionRemappingTable) throws IOException {
                return new TriggeredCircleHitBox(save, positionRemappingTable);
            }
        });
    }

    public TriggeredCircleHitBox(Vector2 position, float radius, float mass, CollisionHandler handler) {
        super(position, radius, mass);
        this.handler = handler;
    }

    private TriggeredCircleHitBox(DataInputStream save, Vector2[] positionRemappingTable) throws IOException {
        super(positionRemappingTable[save.readInt()], save.readFloat(), save.readFloat());

        final int id = save.readInt();
        handler = CollisionHandlerLoader.load(id, save, positionRemappingTable);
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
        handler.run(level, this, thisEntity, other, otherEntity);
    }

    @Override
    public void save(DataOutputStream save, IdentityHashMap<Vector2, Integer> positionRemappingTable) throws IOException {
        super.save(save, positionRemappingTable);
        save.writeInt(handler.getId());
        handler.save(save, positionRemappingTable);
    }

    @Override
    public void update(Level level, int thisEntity) {
        handler.update(level, this, thisEntity);
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {
        handler.handleMessage(level, networkIn, thisEntity);
    }

    @Override
    public int getId() {
        return id;
    }
}
