package wizardike.assignment3.physics.velocity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector2;

/**
 * Constantly moves the entity in the given direction.
 * Created by Isaac on 31/08/2017.
 */
public class Velocity {
    public Vector2 position;
    public float directionX = 0.0f;
    public float directionY = 0.0f;
    public float currentSpeed = 0.0f;

    public Velocity(Vector2 position) {
        this.position = position;
    }

    public Velocity(DataInputStream save, Deserializer deserializer) throws IOException {
        position = deserializer.getObject(save.readInt());
        directionX = save.readFloat();
        directionY = save.readFloat();
        currentSpeed = save.readFloat();
    }

    public void update(float frameTime) {
        position.setX(position.getX() + currentSpeed * directionX * frameTime);
        position.setY(position.getY() + currentSpeed * directionY * frameTime);
    }

    public void save(DataOutputStream saveData, Serializer serializer) throws IOException {
        saveData.writeInt(serializer.getId(position));
        saveData.writeFloat(directionX);
        saveData.writeFloat(directionY);
        saveData.writeFloat(currentSpeed);
    }
}
