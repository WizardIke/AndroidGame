package wizardike.assignment3.Serialization;

import java.util.ArrayList;

import wizardike.assignment3.EntityAllocator;

public class Deserializer {
    private final ArrayList<Object> map = new ArrayList<>();
    private final EntityUpdater entityUpdater;

    public Deserializer(EntityAllocator entityAllocator) {
        entityUpdater = new EntityUpdater(entityAllocator);
    }

    public void addObject(Object object) {
        map.add(object);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(int id) {
        return (T)map.get(id);
    }

    public int getEntity(int oldEntity) {
        return entityUpdater.getEntity(oldEntity);
    }
}
