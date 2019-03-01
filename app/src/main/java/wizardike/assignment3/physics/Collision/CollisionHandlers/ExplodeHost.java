package wizardike.assignment3.physics.Collision.CollisionHandlers;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;
import wizardike.assignment3.physics.Collision.Collidable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static wizardike.assignment3.networking.NetworkMessageTypes.die;

/**
 * Created by Isaac on 14/10/2017.
 */
public class ExplodeHost extends Explode {
    private static final int id = 3;

    static void registerLoader() {
        CollisionHandlerLoader.addLoader(id, new CollisionHandlerLoader.Loader() {
            @Override
            public CollisionHandler load(DataInputStream save, Deserializer deserializer) throws IOException {
                return new ExplodeHost(save, deserializer);
            }
        });
    }

    public ExplodeHost(Vector2 position, float damage, float lifeTime, float vX, float vY) {
        super(position, damage, lifeTime, vX, vY);
    }

    private ExplodeHost(DataInputStream saveData, Deserializer deserializer) throws IOException {
        super(saveData, deserializer);
    }

    @Override
    public void update(Level level, Collidable thisCollidable, int thisEntity) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        timeLeft -= frameTime;
        if(timeLeft < 0.0) {
            level.getDestructionSystem().delayedDestroy(thisEntity);
            final DataOutputStream networkData = level.getEngine().getNetworkConnection().getNetworkOut();
            try {
                networkData.writeInt(12);
                networkData.writeInt(SystemIds.collisionSystem);
                final int index = level.getCollisionSystem().indexOf(thisEntity, thisCollidable);
                networkData.writeInt(index);
                networkData.writeInt(die);
            } catch (IOException e) {
                level.getEngine().onError();
            }
        }
        position.setX(position.getX() + vX * frameTime);
        position.setY(position.getY() + vY * frameTime);
    }

    @Override
    public int getId() {
        return id;
    }
}
