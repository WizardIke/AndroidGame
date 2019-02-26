package wizardike.assignment3.physics.movement;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class PlayerMovementControllerHost extends PlayerMovementController {
    public PlayerMovementControllerHost(Movement movement, float maxSpeed) {
        super(movement, maxSpeed);
    }

    public PlayerMovementControllerHost(DataInputStream save, List<Movement> remappingTable) throws IOException {
        super(save, remappingTable);
    }

    @Override
    public void start(float directionX, float directionY) {
        super.start(directionX, directionY);
        //TODO send message over network
    }

    @Override
    public void move(float directionX, float directionY) {
        super.move(directionX, directionY);
        //TODO send message over network
    }

    @Override
    public void stop(float directionX, float directionY) {
        super.move(directionX, directionY);
        //TODO send message over network
    }
}
