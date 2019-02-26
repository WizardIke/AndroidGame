package wizardike.assignment3.entity;

import android.util.SparseIntArray;

public class EntityUpdater {
    private SparseIntArray map = new SparseIntArray();

    public int getEntity(int oldEntity, EntityAllocator allocator) {
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
