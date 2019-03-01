package wizardike.assignment3.ai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public class BasicAIControllerHostSystem {
    private final ComponentStorage<BasicAIControllerHost> basicAIControllerComponentStorage;

    public BasicAIControllerHostSystem() {
        basicAIControllerComponentStorage = new ComponentStorage<>(BasicAIControllerHost.class);
    }

    public BasicAIControllerHostSystem(DataInputStream save, Deserializer deserializer) throws IOException {
        final int basicAIControllerCount = save.readInt();
        BasicAIControllerHost[] basicAIControllers = new BasicAIControllerHost[basicAIControllerCount];
        for(int i = 0; i != basicAIControllerCount; ++i) {
            basicAIControllers[i] = new BasicAIControllerHost(save, deserializer);
            deserializer.addObject(basicAIControllers[i]);
        }
        int[] basicAIControllerEntities = new int[basicAIControllerCount];
        for(int i = 0; i != basicAIControllerCount; ++i) {
            final int oldEntity = save.readInt();
            basicAIControllerEntities[i] = deserializer.getEntity(oldEntity);
        }
        basicAIControllerComponentStorage = new ComponentStorage<>(BasicAIControllerHost.class,
                basicAIControllerEntities, basicAIControllers);
    }

    public BasicAIControllerHost getBasicAIController(int entity) {
        return basicAIControllerComponentStorage.getComponent(entity);
    }

    public void addBasicAIController(int entity, BasicAIControllerHost basicAIController) {
        basicAIControllerComponentStorage.addComponent(entity, basicAIController);
    }

    public void removeBasicAIControllers(int entity) {
        basicAIControllerComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        final BasicAIControllerHost[] basicAIControllers = basicAIControllerComponentStorage.getAllComponents();
        final int basicAIControllerCount = basicAIControllerComponentStorage.size();
        save.writeInt(basicAIControllerCount);
        for(int i = 0; i != basicAIControllerCount; ++i) {
            basicAIControllers[i].save(save, serializer);
            serializer.addObject(basicAIControllers[i]);
        }

        int[] entities = basicAIControllerComponentStorage.getAllEntities();
        for (int i = 0; i != basicAIControllerCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public void update(Level level) {
        final BasicAIControllerHost[] basicAIControllers = basicAIControllerComponentStorage.getAllComponents();
        int[] entities = basicAIControllerComponentStorage.getAllEntities();
        final int basicAIControllerCount = basicAIControllerComponentStorage.size();
        for(int i = 0; i != basicAIControllerCount; ++i) {
            basicAIControllers[i].update(level, entities[i]);
        }
    }
}
