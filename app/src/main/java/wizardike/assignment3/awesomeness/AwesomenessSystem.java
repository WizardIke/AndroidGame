package wizardike.assignment3.awesomeness;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;

public class AwesomenessSystem {
    private final ComponentStorage<Awesomeness> awesomenessComponentStorage;

    public AwesomenessSystem() {
        awesomenessComponentStorage = new ComponentStorage<>(Awesomeness.class);
    }

    public AwesomenessSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int awesomenessCount = save.readInt();
        Awesomeness[] Awesomenesses = new Awesomeness[awesomenessCount];
        for(int i = 0; i != awesomenessCount; ++i) {
            Awesomenesses[i] = new Awesomeness(save);
        }
        int[] awesomenessEntities = new int[awesomenessCount];
        for(int i = 0; i != awesomenessCount; ++i) {
            final int oldEntity = save.readInt();
            awesomenessEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        awesomenessComponentStorage = new ComponentStorage<>(Awesomeness.class, awesomenessEntities, Awesomenesses);
    }

    public Awesomeness getAwesomeness(int entity) {
        return awesomenessComponentStorage.getComponent(entity);
    }

    public void addAwesomeness(int entity, Awesomeness awesomeness) {
        awesomenessComponentStorage.addComponent(entity, awesomeness);
    }

    public void removeAwesomenesses(int entity) {
        awesomenessComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save) throws IOException {
        final Awesomeness[] Awesomenesses = awesomenessComponentStorage.getAllComponents();
        final int awesomenessCount = awesomenessComponentStorage.size();
        save.writeInt(awesomenessCount);
        for(int i = 0; i != awesomenessCount; ++i) {
            Awesomenesses[i].save(save);
        }

        int[] entities = awesomenessComponentStorage.getAllEntities();
        for (int i = 0; i != awesomenessCount; ++i) {
            save.writeInt(entities[i]);
        }
    }
}
