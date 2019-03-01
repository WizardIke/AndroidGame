package wizardike.assignment3.Serialization;

import android.util.SparseIntArray;

import wizardike.assignment3.EntityAllocator;

public class EntityUpdater {
    private SparseIntArray map = new SparseIntArray();
    private final EntityAllocator allocator;

    EntityUpdater(EntityAllocator allocator) {
        this.allocator = allocator;
    }

    public int getEntity(int oldEntity) {
        final int index = map.indexOfKey(oldEntity);
        if(index < 0) {
            final int newEntity = allocator.allocate();
            map.put(oldEntity, newEntity);
            return newEntity;
        } else {
            return map.valueAt(index);
        }
    }
}
