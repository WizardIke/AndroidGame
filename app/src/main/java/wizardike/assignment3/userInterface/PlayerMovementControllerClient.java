package wizardike.assignment3.userInterface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.velocity.Velocity;

public class PlayerMovementControllerClient implements AnalogStickListener {
    private static final int id = 2;

    static void registerLoader() {
        AnalogStickListenerLoader.addLoader(id, new AnalogStickListenerLoader.Loader() {
            @Override
            public AnalogStickListener load(DataInputStream save, int entity, Level level, Deserializer deserializer) throws IOException {
                return new PlayerMovementControllerClient(save, entity, level, deserializer);
            }
        });
    }

    private final Velocity velocity;

    public PlayerMovementControllerClient(Vector2 position, float maxSpeed) {
        velocity = new Velocity(position);
        velocity.currentSpeed = maxSpeed;
    }

    private PlayerMovementControllerClient(DataInputStream save, int entity, Level level, Deserializer deserializer) throws IOException {
        boolean moving = save.readBoolean();
        if(moving) {
            velocity = deserializer.getObject(save.readInt());
            //stop moving as user might not be pressing move button anymore.
            level.getVelocitySystem().removeVelocity(entity, velocity);
        } else {
            velocity = new Velocity(save, deserializer);
        }
    }

    @Override
    public void save(DataOutputStream save, int entity, Level level, Serializer serializer) throws IOException {
        final int index = level.getVelocitySystem().indexOf(entity, velocity);
        if(index == -1) {
            save.writeBoolean(false);
            velocity.save(save, serializer);
        } else {
            save.writeBoolean(true);
            save.writeInt(serializer.getId(velocity));
        }
    }

    @Override
    public void start(int entity, Level level, float directionX, float directionY) {
        velocity.directionX = directionX;
        velocity.directionY = directionY;
        level.getVelocitySystem().addVelocity(entity, velocity);
        //TODO send message over network
    }

    @Override
    public void move(int entity, Level level, float directionX, float directionY) {
        velocity.directionX = directionX;
        velocity.directionY = directionY;
        //TODO send message over network
    }

    @Override
    public void stop(int entity, Level level, float directionX, float directionY) {
        velocity.directionX = directionX;
        velocity.directionY = directionY;
        level.getVelocitySystem().removeVelocity(entity, velocity);
        //TODO send message over network
    }

    @Override
    public int getId() {
        return id;
    }
}
