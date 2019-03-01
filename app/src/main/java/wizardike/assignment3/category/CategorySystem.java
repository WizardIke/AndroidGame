package wizardike.assignment3.category;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;

public class CategorySystem {
    private final ComponentStorage<Integer> categoryComponentStorage;

    public CategorySystem() {
        categoryComponentStorage = new ComponentStorage<>(Integer.class);
    }

    public CategorySystem(DataInputStream save, Deserializer deserializer) throws IOException {
        final int categoryCount = save.readInt();
        Integer[] categories = new Integer[categoryCount];
        for(int i = 0; i != categoryCount; ++i) {
            categories[i] = save.readInt();
            deserializer.addObject(categories[i]);
        }
        int[] categoryEntities = new int[categoryCount];
        for(int i = 0; i != categoryCount; ++i) {
            final int oldEntity = save.readInt();
            categoryEntities[i] = deserializer.getEntity(oldEntity);
        }
        categoryComponentStorage = new ComponentStorage<>(Integer.class, categoryEntities, categories);
    }

    public Integer getCategory(int entity) {
        return categoryComponentStorage.getComponent(entity);
    }

    public void addCategory(int entity, Integer category) {
        categoryComponentStorage.addComponent(entity, category);
    }

    public void removeCategories(int entity) {
        categoryComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        final Integer[] categories = categoryComponentStorage.getAllComponents();
        final int categoryCount = categoryComponentStorage.size();
        save.writeInt(categoryCount);
        for(int i = 0; i != categoryCount; ++i) {
            save.writeInt(categories[i]);
            serializer.addObject(categories[i]);
        }

        int[] entities = categoryComponentStorage.getAllEntities();
        for (int i = 0; i != categoryCount; ++i) {
            save.writeInt(entities[i]);
        }
    }
}
