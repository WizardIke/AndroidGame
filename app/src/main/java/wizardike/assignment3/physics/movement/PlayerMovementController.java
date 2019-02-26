package wizardike.assignment3.physics.movement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;

import wizardike.assignment3.AnalogStick;

/**
 * Created by Isaac on 31/08/2017.
 */
public class PlayerMovementController implements AnalogStick.OnRotationListener {
    protected Movement movement;
    protected float maxSpeed;

    public PlayerMovementController(Movement movement, float maxSpeed) {
        this.movement = movement;
        this.maxSpeed = maxSpeed;
    }

    public PlayerMovementController(DataInputStream save, final List<Movement> remappingTable) throws IOException {
        movement = remappingTable.get(save.readInt());
        maxSpeed = save.readFloat();
    }

    public void save(DataOutputStream save, final IdentityHashMap<Movement, Integer> remappingTable) throws IOException {
        save.writeInt(remappingTable.get(movement));
        save.writeFloat(maxSpeed);
    }

    @Override
    public void start(float directionX, float directionY) {
        movement.directionX = directionX;
        movement.directionY = directionY;
        movement.currentSpeed = maxSpeed;
    }

    @Override
    public void move(float directionX, float directionY) {
        movement.directionX = directionX;
        movement.directionY = directionY;
    }

    @Override
    public void stop(float directionX, float directionY) {
        movement.directionX = directionX;
        movement.directionY = directionY;
        movement.currentSpeed = 0.0f;
    }
}
