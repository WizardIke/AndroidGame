package wizardike.assignment3;

import android.util.SparseArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.entities.EntityGenerator;
import wizardike.assignment3.entities.EntityLoader;

/**
 * Used to load and unload Entities asynchronously.
 * If the entity is in the save file, it will be loaded using the EntityLoader
 * otherwise when it is first needed it will be created using the EntityGenerator and saved to
 * the save file if it has a Savable component.
 */
public class EntityStreamingManager {
    public static abstract class Request {
        Request previous;
        Request next;

        protected abstract void onLoadComplete(Entity entity);
    }

    private static final class EntityDescriptor {
        Entity entity;
        int referenceCount = 1;
        boolean loaded = false;
        Request requests;

        EntityDescriptor(Request request) {
            request.previous = null;
            request.next = null;
            requests = request;
        }

        void addLoadRequest(Request request) {
            referenceCount += 1;
            if(loaded) {
                requests.onLoadComplete(entity);
            } else {
                request.previous = null;
                request.next = requests;
                requests.previous = request;
                requests = request;
            }
        }

        /**
         * @param request a pending request that isn't needed anymore
         */
        void cancelLoadRequest(Request request) {
            if(request.next != null) {
                request.next.previous = request.previous;
            }
            if(request.previous != null) {
                request.previous.next = request.next;
            } else {
                requests = request.next;
            }
            referenceCount -= 1;
        }

        /**
         * @return true if the entity can be unloaded, false otherwise.
         */
        boolean addUnloadRequest() {
            referenceCount -= 1;
            if(referenceCount == 0) {
                loaded = false;
                return true;
            } else {
                return false;
            }
        }

        /**
         * Should be called when the entity finishes loading
         * @return true if the entity should be unloaded, false otherwise.
         */
        boolean onLoadComplete(Entity entity) {
            if(requests != null) {
                loaded = true;
                do {
                    requests.onLoadComplete(entity);
                    requests = requests.next;
                } while(requests != null);
                return false;
            } else {
                return true;
            }
        }

        /**
         * Should be called when the entity finishes unloading
         * @return true if the entity should be loaded, false otherwise.
         */
        boolean onUnloadComplete() {
            return requests != null;
        }
    }

    private final SparseArray<EntityDescriptor> levelDescriptors = new SparseArray<>();
    private final SparseArray<byte[]> entityData = new SparseArray<>();
    private final Engine engine;

    public EntityStreamingManager(Engine world) {
        this.engine = world;
    }

    /**
     * Must not be called while an entity is loading or unloading
     */
    public void addEntityData(DataInputStream data) throws IOException {
        final int numberOfEntities = data.readInt();
        for(int i = 0; i < numberOfEntities; ++i) {
            final int length = data.readInt();
            final byte[] bytes = new byte[length];
            int lengthRead = data.read(bytes);
            int entityID =  bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
            if(lengthRead != length){
                throw new IOException("Read a length of " + lengthRead + " when " + length
                        + " bytes were meant to be read.");
            }
            entityData.put(entityID, bytes);
        }
    }

    /**
     * Can be called from any thread if entity save functions can be
     */
    public void save(DataOutputStream saveData) throws IOException {
        final int descriptorsSize = levelDescriptors.size();
        for(int i = 0; i < descriptorsSize; ++i) {
            final int entityID = levelDescriptors.keyAt(i);
            EntityDescriptor descriptor = levelDescriptors.valueAt(i);
            if(descriptor.loaded) {
                saveEntityData(descriptor.entity, entityID);
            }
        }
        final int numberOfEntities = entityData.size();
        saveData.writeInt(numberOfEntities);
        for(int i = 0; i < numberOfEntities; ++i) {
            final byte[] data = entityData.valueAt(i);
            saveData.writeInt(data.length);
            saveData.write(data);
        }
    }

    /**
     * Should be called on the worker thread
     */
    public void loadEntity(int entityID, Request request) {
        int index = levelDescriptors.indexOfKey(entityID);
        if(index >= 0) {
            EntityDescriptor descriptor = levelDescriptors.valueAt(index);
            descriptor.addLoadRequest(request);
        } else {
            EntityDescriptor descriptor = new EntityDescriptor(request);
            levelDescriptors.put(entityID, descriptor);
            loadEntityUniqueRequest(entityID, descriptor);
        }
    }

    /**
     * Should be called on the worker thread
     */
    public void cancelLoadEntity(int entityID, Request request) {
        EntityDescriptor descriptor = levelDescriptors.get(entityID);
        descriptor.cancelLoadRequest(request);
    }

    /**
     * Should be called on the worker thread
     */
    public void unloadEntity(int entityID) {
        EntityDescriptor descriptor = levelDescriptors.get(entityID);
        boolean canUnload = descriptor.addUnloadRequest();
        if(canUnload) {
            unloadEntityUniqueRequest(entityID, descriptor);
        }
    }

    private void loadEntityUniqueRequest(final int entityID, final EntityDescriptor descriptor) {
        byte[] data = entityData.get(entityID);
        if(data != null) {
            final DataInputStream dataReader = new DataInputStream(new ByteArrayInputStream(data));
            try {
                dataReader.readInt(); //remove id
                EntityLoader.loadEntity(entityID, dataReader, engine, new EntityLoader.EntityLoadedCallback() {
                    @Override
                    public void onLoadComplete(Entity entity) {
                        loadEntityUniqueRequestFinished(descriptor, entityID, entity);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            EntityGenerator.generateEntity(entityID, engine, new EntityGenerator.Callback() {
                @Override
                public void onLoadComplete(Entity entity) {
                    loadEntityUniqueRequestFinished(descriptor, entityID, entity);
                }
            });
        }
    }

    private void loadEntityUniqueRequestFinished(EntityDescriptor descriptor, int entityID, Entity entity) {
        boolean shouldUnload;
        shouldUnload = descriptor.onLoadComplete(entity);
        if(shouldUnload) {
            unloadEntityUniqueRequest(entityID, descriptor);
        }
    }

    private void unloadEntityUniqueRequest(int entityID, EntityDescriptor descriptor) {
        Entity entity = descriptor.entity;
        descriptor.entity = null;
        saveEntityData(entity, entityID);

        boolean shouldLoad;
        shouldLoad = descriptor.onUnloadComplete();
        if(shouldLoad) {
            loadEntityUniqueRequest(entityID, descriptor);
        }
    }

    private void saveEntityData(Entity entity, int entityID) {
        Savable saveFunc = entity.getComponent(Savable.class);
        if(saveFunc != null) {
            ByteArrayOutputStream entityDataStream = new ByteArrayOutputStream();
            DataOutputStream entityDataWriter = new DataOutputStream(entityDataStream);
            try {
                saveFunc.save(entityDataWriter);
                entityDataWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] result = entityDataStream.toByteArray();
            //must be synchronized encase the entity is saving and unloading at the same time
            synchronized (entityData) {
                entityData.put(entityID, result);
            }
        }
    }
}
