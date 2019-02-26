package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.geometry.Vector2;

public class Camera {
    public Vector2 position;
    public float zoom;

    public Camera(Vector2 position, float zoom) {
        this.position = position;
        this.zoom = zoom;
    }

    public Camera(DataInputStream save, Vector2[] positionRemappingTable) throws IOException {
        int positionIndex = save.readInt();
        position = positionIndex == -1 ? null : positionRemappingTable[positionIndex];
        zoom = save.readFloat();
    }

    public void save(DataOutputStream save, IdentityHashMap<Vector2, Integer> positionRemappingTable) throws IOException {
        save.writeInt(position != null ? positionRemappingTable.get(position) : -1);
        save.writeFloat(zoom);
    }
}
