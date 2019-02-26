package wizardike.assignment3.physics.movement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

import wizardike.assignment3.geometry.Vector2;

/**
 * Constantly moves the entity in the given direction.
 * Created by Isaac on 31/08/2017.
 */
public class Movement {
    public Vector2 position;
    public float directionX = 0.0f;
    public float directionY = 0.0f;
    public float currentSpeed = 0.0f;

    public Movement(Vector2 position) {
        this.position = position;
    }

    public Movement(DataInputStream save, final Vector2[] remappingTable) throws IOException {
        position = remappingTable[save.readInt()];
        directionX = save.readFloat();
        directionY = save.readFloat();
        currentSpeed = save.readFloat();
    }

    public void update(float frameTime) {
        position.setX(position.getX() + currentSpeed * directionX * frameTime);
        position.setY(position.getY() + currentSpeed * directionY * frameTime);
    }

    public void save(DataOutputStream saveData, final IdentityHashMap<Vector2, Integer> remappingTable) throws IOException {
        saveData.writeInt(remappingTable.get(position));
        saveData.writeFloat(directionX);
        saveData.writeFloat(directionY);
        saveData.writeFloat(currentSpeed);
    }

    //TODO addCollidable message handling
}
