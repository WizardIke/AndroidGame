package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector2;

public class Camera {
    public Vector2 position;
    public float zoom;

    public Camera(Vector2 position, float zoom) {
        this.position = position;
        this.zoom = zoom;
    }

    public Camera(DataInputStream save, Deserializer deserializer) throws IOException {
        int positionIndex = save.readInt();
        position = positionIndex == -1 ? null : (Vector2)deserializer.getObject(positionIndex);
        zoom = save.readFloat();
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeInt(position != null ? serializer.getId(position) : -1);
        save.writeFloat(zoom);
    }
}
