package wizardike.assignment3.userInterface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.AnalogStick;
import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;

public class UserInterfaceSystem {
    private class AnalogStickSystem implements AnalogStick.OnRotationListener {
        ComponentStorage<AnalogStickListener> analogStickListenerComponentStorage;
        Level level;

        AnalogStickSystem(Level level) {
            this.level = level;
            analogStickListenerComponentStorage = new ComponentStorage<>(AnalogStickListener.class);
        }

        AnalogStickSystem(DataInputStream save, Level level, Deserializer deserializer) throws IOException {
            this.level = level;

            final int count = save.readInt();
            int[] entities = new int[count];
            for(int i = 0; i != count; ++i) {
                final int oldEntity = save.readInt();
                entities[i] = deserializer.getEntity(oldEntity);
            }

            AnalogStickListener[] analogStickListeners = new AnalogStickListener[count];
            for(int i = 0; i != count; ++i) {
                final int id = save.readInt();
                analogStickListeners[i] = AnalogStickListenerLoader.load(id, save, entities[i], level, deserializer);
                deserializer.addObject(analogStickListeners[i]);
            }

            analogStickListenerComponentStorage = new ComponentStorage<>(AnalogStickListener.class, entities, analogStickListeners);
        }

        void addAnalogStickListener(int entity, AnalogStickListener analogStickListener) {
            analogStickListenerComponentStorage.addComponent(entity, analogStickListener);
        }

        void removeAnalogStickListeners(int entity) {
            analogStickListenerComponentStorage.removeComponents(entity);
        }

        void save(DataOutputStream save, Serializer serializer) throws IOException {
            final AnalogStickListener[] analogStickListeners = analogStickListenerComponentStorage.getAllComponents();
            final int[] entities = analogStickListenerComponentStorage.getAllEntities();
            final int count = analogStickListenerComponentStorage.size();
            save.writeInt(count);
            for (int i = 0; i != count; ++i) {
                save.writeInt(entities[i]);
            }

            for(int i = 0; i != count; ++i) {
                save.writeInt(analogStickListeners[i].getId());
                analogStickListeners[i].save(save, entities[i], level, serializer);
                serializer.addObject(analogStickListeners[i]);
            }
        }

        @Override
        public void start(final float directionX, final float directionY) {
            level.getEngine().getGraphicsManager().queueEvent(new Runnable() {
                @Override
                public void run() {
                    final AnalogStickListener[] analogStickListeners = analogStickListenerComponentStorage.getAllComponents();
                    int[] entities = analogStickListenerComponentStorage.getAllEntities();
                    final int count = analogStickListenerComponentStorage.size();
                    for(int i = 0; i != count; ++i) {
                        analogStickListeners[i].start(entities[i], level, directionX, directionY);
                    }
                }
            });
        }

        @Override
        public void move(final float directionX, final float directionY) {
            level.getEngine().getGraphicsManager().queueEvent(new Runnable() {
                @Override
                public void run() {
                    final AnalogStickListener[] analogStickListeners = analogStickListenerComponentStorage.getAllComponents();
                    int[] entities = analogStickListenerComponentStorage.getAllEntities();
                    final int count = analogStickListenerComponentStorage.size();
                    for(int i = 0; i != count; ++i) {
                        analogStickListeners[i].move(entities[i], level, directionX, directionY);
                    }
                }
            });
        }

        @Override
        public void stop(final float directionX, final float directionY) {
            level.getEngine().getGraphicsManager().queueEvent(new Runnable() {
                @Override
                public void run() {
                    final AnalogStickListener[] analogStickListeners = analogStickListenerComponentStorage.getAllComponents();
                    int[] entities = analogStickListenerComponentStorage.getAllEntities();
                    final int count = analogStickListenerComponentStorage.size();
                    for(int i = 0; i != count; ++i) {
                        analogStickListeners[i].stop(entities[i], level, directionX, directionY);
                    }
                }
            });
        }
    }

    private AnalogStickSystem leftAnalogStickSystem;
    private AnalogStickSystem rightAnalogStickSystem;

    public UserInterfaceSystem(Level level) {
        leftAnalogStickSystem = new AnalogStickSystem(level);
        rightAnalogStickSystem = new AnalogStickSystem(level);
    }

    public UserInterfaceSystem(DataInputStream save, Level level, Deserializer deserializer) throws IOException {
        leftAnalogStickSystem = new AnalogStickSystem(save, level, deserializer);
        rightAnalogStickSystem = new AnalogStickSystem(save, level, deserializer);
    }

    public void start(UserInterface userInterface) {
        userInterface.addLeftAnalogStickOnRotationListener(leftAnalogStickSystem);
        userInterface.addRightAnalogStickOnRotationListener(rightAnalogStickSystem);
    }

    public void stop(UserInterface userInterface) {
        userInterface.removeLeftAnalogStickOnRotationListener(leftAnalogStickSystem);
        userInterface.removeRightAnalogStickOnRotationListener(rightAnalogStickSystem);
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        leftAnalogStickSystem.save(save, serializer);
        rightAnalogStickSystem.save(save, serializer);
    }

    public void addLeftAnalogStickListener(int entity, AnalogStickListener analogStickListener) {
        leftAnalogStickSystem.addAnalogStickListener(entity, analogStickListener);
    }

    public void addRightAnalogStickListener(int entity, AnalogStickListener analogStickListener) {
        rightAnalogStickSystem.addAnalogStickListener(entity, analogStickListener);
    }

    public void removeLeftAnalogStickListeners(int entity) {
        leftAnalogStickSystem.removeAnalogStickListeners(entity);
    }

    public void removeRightAnalogStickListeners(int entity) {
        rightAnalogStickSystem.removeAnalogStickListeners(entity);
    }
}
