package wizardike.assignment3.levels;

import android.util.SparseArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;

/**
 * Used to load and unload Levels asynchronously.
 * If the level is in the save file, it will be loaded using the it's constructor that takes a
 * DataInputStream, otherwise when it is first needed it will be created using the LevelGenerator
 * and saved to the save file.
 */
public class LevelStreamingManager {
    public static abstract class Request {
        Request previous;
        Request next;

        protected abstract void onLoadComplete(Level level);
    }

    private static final class Descriptor {
        Level level;
        int referenceCount = 1;
        boolean loaded = false;
        Request requests;

        Descriptor(Request request) {
            request.previous = null;
            request.next = null;
            requests = request;
        }

        void addLoadRequest(Request request) {
            referenceCount += 1;
            if(loaded) {
                requests.onLoadComplete(level);
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
        boolean onLoadComplete(Level level) {
            if(requests != null) {
                loaded = true;
                this.level = level;
                do {
                    requests.onLoadComplete(level);
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

    private final SparseArray<Descriptor> descriptors = new SparseArray<>();
    private final SparseArray<byte[]> saveData = new SparseArray<>();
    private final Engine engine;

    public LevelStreamingManager(Engine engine, DataInputStream data) throws IOException {
        this.engine = engine;
        loadData(data);
    }

    /**
     * Must not be called while a level is loading or unloading
     */
    private void loadData(DataInputStream data) throws IOException {
        final int levelCount = data.readInt();
        for(int i = 0; i != levelCount; ++i) {
            final int levelId = data.readInt();
            final int length = data.readInt();
            final byte[] bytes = new byte[length];
            int lengthRead = data.read(bytes);
            if(lengthRead != length){
                throw new IOException("Read a length of " + lengthRead + " when " + length
                        + " bytes were meant to be read.");
            }
            saveData.put(levelId, bytes);
        }
    }

    /**
     * Can be called from any thread if level save functions can be
     */
    public void save(DataOutputStream save) throws IOException {
        synchronized (descriptors) {
            final int descriptorsSize = descriptors.size();
            for(int i = 0; i != descriptorsSize; ++i) {
                final int levelId = descriptors.keyAt(i);
                Descriptor descriptor = descriptors.valueAt(i);
                if(descriptor.loaded) {
                    saveData(descriptor.level, levelId);
                }
            }
        }
        synchronized (saveData) {
            final int levelCount = saveData.size();
            save.writeInt(levelCount);
            for(int i = 0; i != levelCount; ++i) {
                final byte[] data = saveData.valueAt(i);
                final int levelId = saveData.keyAt(i);
                save.writeInt(levelId);
                save.writeInt(data.length);
                save.write(data);
            }
        }
    }

    /**
     * Can be called from any thread. Should be called on a worker thread
     */
    public void loadLevel(int entityID, Request request) {
        int index;
        Descriptor descriptor;
        synchronized (descriptors) {
            index = descriptors.indexOfKey(entityID);
            if(index >= 0) {
                descriptor = descriptors.valueAt(index);
                descriptor.addLoadRequest(request);
            } else {
                descriptor = new Descriptor(request);
                descriptors.put(entityID, descriptor);
            }
        }

        if(index < 0) {
            loadUniqueRequest(entityID, descriptor);
        }
    }

    /**
     * Can be called from any thread. Should be called on a worker thread
     */
    public void cancelLoadLevel(int entityID, Request request) {
        synchronized (descriptors) {
            Descriptor descriptor = descriptors.get(entityID);
            descriptor.cancelLoadRequest(request);
        }
    }

    /**
     * Can be called from any thread. Should be called on a worker thread
     */
    public void unloadLevel(int entityID) {
        Descriptor descriptor;
        boolean canUnload;
        synchronized (descriptors) {
            descriptor = descriptors.get(entityID);
            canUnload = descriptor.addUnloadRequest();
        }
        if(canUnload) {
            unloadUniqueRequest(entityID, descriptor);
        }
    }

    private void loadUniqueRequest(final int levelId, final Descriptor descriptor) {
        byte[] data;
        synchronized (saveData) {
            data = saveData.get(levelId);
        }
        if(data != null) {
            final DataInputStream dataReader = new DataInputStream(new ByteArrayInputStream(data));
            try {
                new Level(dataReader, engine, new Level.Callback() {
                    @Override
                    public void onLoadComplete(Level level) {
                        loadUniqueRequestFinished(descriptor, levelId, level);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LevelGenerator.generate(levelId, engine, new LevelGenerator.Callback() {
                @Override
                public void onLoadComplete(Level entity) {
                    loadUniqueRequestFinished(descriptor, levelId, entity);
                }
            });
        }
    }

    private void loadUniqueRequestFinished(Descriptor descriptor, int levelId, Level entity) {
        boolean shouldUnload;
        synchronized (descriptors) {
            shouldUnload = descriptor.onLoadComplete(entity);
        }
        if(shouldUnload) {
            unloadUniqueRequest(levelId, descriptor);
        }
    }

    private void unloadUniqueRequest(int levelId, Descriptor descriptor) {
        Level level = descriptor.level;
        descriptor.level = null;
        saveData(level, levelId);

        boolean shouldLoad;
        synchronized (descriptors) {
            shouldLoad = descriptor.onUnloadComplete();
        }
        if(shouldLoad) {
            loadUniqueRequest(levelId, descriptor);
        }
    }

    private void saveData(Level level, int levelId) {
        ByteArrayOutputStream entityDataStream = new ByteArrayOutputStream();
        DataOutputStream entityDataWriter = new DataOutputStream(entityDataStream);
        try {
            level.save(entityDataWriter);
            entityDataWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result = entityDataStream.toByteArray();
        //must be synchronized encase the entity is saving and unloading at the same time
        synchronized (saveData) {
            saveData.put(levelId, result);
        }
    }
}
