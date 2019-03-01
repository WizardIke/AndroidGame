package wizardike.assignment3.position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector2;

public class PositionSystem {
    private final ComponentStorage<Vector2> positionComponentStorage;

    public PositionSystem() {
        positionComponentStorage = new ComponentStorage<>(Vector2.class);
    }

    public PositionSystem(DataInputStream save, Deserializer deserializer) throws IOException {
        final int positionCount = save.readInt();
        Vector2[] positions = new Vector2[positionCount];
        for(int i = 0; i != positionCount; ++i) {
            positions[i] = new Vector2(save);
            deserializer.addObject(positions[i]);
        }
        int[] positionEntities = new int[positionCount];
        for(int i = 0; i != positionCount; ++i) {
            final int oldEntity = save.readInt();
            positionEntities[i] = deserializer.getEntity(oldEntity);
        }
        positionComponentStorage = new ComponentStorage<>(Vector2.class, positionEntities, positions);
    }

    public Vector2 getPosition(int entity) {
        return positionComponentStorage.getComponent(entity);
    }

    public void addPosition(int entity, Vector2 position) {
        positionComponentStorage.addComponent(entity, position);
    }

    public void removePositions(int entity) {
        positionComponentStorage.removeComponents(entity);
    }

    public int[] getEntities() {
        return positionComponentStorage.getAllEntities();
    }

    public int positionsCount() {
        return positionComponentStorage.size();
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        final Vector2[] positions = positionComponentStorage.getAllComponents();
        final int positionCount = positionComponentStorage.size();
        save.writeInt(positionCount);
        for(int i = 0; i != positionCount; ++i) {
            positions[i].save(save);
            serializer.addObject(positions[i]);
        }

        int[] entities = positionComponentStorage.getAllEntities();
        for (int i = 0; i != positionCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public int indexOf(int entity, Vector2 position) {
        return positionComponentStorage.indexOf(entity, position);
    }

    public void handleMessage(DataInputStream networkIn) throws IOException {
        Vector2 position = positionComponentStorage.getAllComponents()[networkIn.readInt()];
        position.setX(networkIn.readFloat());
        position.setY(networkIn.readFloat());
    }
}
