package wizardike.assignment3.ai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;

import wizardike.assignment3.AnalogStick;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.physics.movement.Movement;

/**
 * Created by Isaac on 31/08/2017.
 */
public class PlayerMovementController implements AnalogStick.OnRotationListener {
    protected Movement movement;

    public PlayerMovementController(Vector2 position, float maxSpeed) {
        this.movement = new Movement(position);
        movement.currentSpeed = maxSpeed;
    }

    public PlayerMovementController(DataInputStream save, final Vector2[] remappingTable) throws IOException {
        movement = new Movement(save, remappingTable);
    }

    public void save(DataOutputStream save, final IdentityHashMap<Vector2, Integer> remappingTable) throws IOException {
        movement.save(save, remappingTable);
    }

    @Override
    public void start(float directionX, float directionY) {
        movement.directionX = directionX;
        movement.directionY = directionY;

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
    }
}
