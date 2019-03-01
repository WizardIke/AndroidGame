package wizardike.assignment3;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;

import wizardike.assignment3.geometry.Vector2;

/**
 * Stores components of one type for multiple entities in an array for fast iteration while
 * supporting fairly fast addition and removal.
 * @param <E> The type of component to store
 */
@SuppressWarnings("unchecked")
public class ComponentStorage<E> {
    private SparseArray<int[]> lookup;
    private E[] components;
    private int[] entities;
    private int size;
    private int capacity;
    private Class<E> arrayCreator;

    public ComponentStorage(Class<E> arrayCreator) {
        this.arrayCreator = arrayCreator;
        initEmpty();
    }

    public ComponentStorage(Class<E> arrayCreator, int[] entities, E[] components) {
        this.arrayCreator = arrayCreator;
        final int count = entities.length;
        if(count == 0) {
            initEmpty();
        } else {
            lookup = new SparseArray<>();
            this.entities = entities;
            this.components = components;
            size = count;
            capacity = count;

            for(int i = 0; i != count; ++i) {
                addEntityToLookUp(entities[i], i);
            }
        }
    }

    private void initEmpty() {
        lookup = new SparseArray<>();
        components = null;
        entities = null;
        size = 0;
        capacity = 0;
    }

    /**
     * @param entity The entity to associate with the component for lookup later
     * @param component The component to insert
     */
    public void addComponent(int entity, E component) {
        if(size == capacity) {
            increaseCapacity();
        }
        final int index = size;
        components[index] = component;
        entities[index] = entity;
        ++size;

        addEntityToLookUp(entity, index);
    }

    private void addEntityToLookUp(int entity, int index) {
        int[] componentsForEntity = lookup.get(entity, null);
        if(componentsForEntity != null) {
            int[] newComponentsForEntity = new int[componentsForEntity.length + 1];
            for(int i = 0; i != componentsForEntity.length; ++i) {
                newComponentsForEntity[i] = componentsForEntity[i];
            }
            newComponentsForEntity[componentsForEntity.length] = index;
            lookup.put(entity, newComponentsForEntity);
        } else {
            lookup.put(entity, new int[]{index});
        }
    }

    /**
     * Removes all components in this ComponentStorage from an entity.
     * Can be called even if the entity doesn't have any components of this type.
     * @param entity The entity to remove components from
     */
    public void removeComponents(int entity) {
        int[] indices = lookup.get(entity);
        if(indices == null) return;
        for(int index : indices) {
            moveLastComponent(index);
            --size;
        }
        lookup.remove(entity);
    }

    /**
     * Removes all components in this ComponentStorage from an entity.
     * All remaining components are left in the same order.
     * Can be called even if the entity doesn't have any components of this type.
     * @param entity The entity to remove components from
     */
    public void removeComponentsKeepingOrdering(int entity) {
        int[] indices = lookup.get(entity);
        if(indices == null) return;
        Arrays.sort(indices);
        int shift = 0;
        for(int i = 0; i != size; ++i) {
            if(shift != indices.length && indices[shift] == i) {
                ++shift;
            } else {
                moveComponent(i, i - shift);
            }
        }
        lookup.remove(entity);
        size -= indices.length;
    }

    /**
     * Removes the component in this ComponentStorage from an entity.
     * Can be called even if the entity doesn't have this component.
     * @param entity The entity to remove the component from
     */
    public void removeComponent(int entity, E component) {
        int[] indices = lookup.get(entity);
        if(indices == null) return;
        int i = 0;
        for(;; ++i) {
            if(i == indices.length) return;
            final int index = indices[i];
            if(components[index] == component) {
                moveLastComponent(index);
                --size;
                break;
            }
        }
        final int newLength = indices.length - 1;
        if(newLength != 0) {
            indices[i] = indices[newLength];
            int[] newIndices = new int[newLength];
            for(int j = 0; j != newLength; ++j) {
                newIndices[j] = indices[j];
            }
            lookup.put(entity, newIndices);
        } else {
            lookup.remove(entity);
        }
    }

    private void moveLastComponent(int newIndex) {
        //moved last component into the position newIndex
        moveComponent(size - 1, newIndex);
    }

    private void moveComponent(int from, int to) {
        components[to] = components[from];
        entities[to] = entities[from];

        //let last entity know that its component moved
        int[] indicesToAdjust = lookup.get(entities[from]);
        for(int i = 0; i != indicesToAdjust.length; ++i) {
            if(indicesToAdjust[i] == from) {
                indicesToAdjust[i] = to;
                break;
            }
        }
    }

    /**
     * @param entity The entity to get the component for
     * @return A component owned by the entity
     */
    public E getComponent(int entity) {
        final int[] indices = lookup.get(entity, null);
        return indices != null ? components[indices[0]] : null;
    }

    /**
     * @param entity The entity to get the components for
     * @return All components owned by the entity
     */
    public Iterable<E> getComponents(int entity) {
        final int[] indices = lookup.get(entity, null);
        if(indices == null) return null;

        final E[] components = this.components;
        return new Iterable<E>() {
            @NonNull
            @Override
            public Iterator iterator() {
                return new Iterator<E>() {
                    private int index = 0;

                    public boolean hasNext() {
                        return index != indices.length;
                    }
                    public E next() {
                        final E element = components[indices[index]];
                        ++index;
                        return element;
                    }
                };
            }
        };
    }

    /**
     * @return All components stored in this ComponentStorage.
     * Note the array might be longer than the number of components.
     */
    public E[] getAllComponents() {
        return components;
    }

    /**
     * @return An array of entities where the entity at each index owns the component at the same index.
     * Note the array might be longer than the number of components.
     */
    public int[] getAllEntities() {
        return entities;
    }

    public int size() {
        return size;
    }

    /**
     * Swaps the position of two components. Can be used for e.g. sorting
     */
    public void swapComponents(final int index1, final int index2) {
        final E tempComponent = components[index1];
        final int tempEntity = entities[index1];
        components[index1] = components[index2];
        entities[index1] = entities[index2];
        components[index2] = tempComponent;
        entities[index2] = tempEntity;

        int[] indicesToAdjust = lookup.get(tempEntity); //entity that used to be at index1
        for(int i = 0; i != indicesToAdjust.length; ++i) {
            if(indicesToAdjust[i] == index1) {
                indicesToAdjust[i] = index2;
                break;
            }
        }

        indicesToAdjust = lookup.get(entities[index1]); //entity that used to be at index2
        for(int i = 0; i != indicesToAdjust.length; ++i) {
            if(indicesToAdjust[i] == index2) {
                indicesToAdjust[i] = index1;
                break;
            }
        }
    }

    private void increaseCapacity() {
        capacity = capacity + (capacity >> 1) + 1;
        final E[] newComponents = (E[])Array.newInstance(arrayCreator, capacity);
        final int oldSize = size;
        final E[] oldComponents = components;
        for(int i = 0; i != oldSize; ++i) {
            newComponents[i] = oldComponents[i];
        }
        components = newComponents;

        final int[] newEntities = new int[capacity];
        final int[] oldEntities = entities;
        for(int i = 0; i != oldSize; ++i) {
            newEntities[i] = oldEntities[i];
        }
        entities = newEntities;
    }

    public int indexOf(int entity, E component) {
        int[] indices = lookup.get(entity);
        if(indices == null) return -1;
        for(int index : indices) {
            if(components[index] == component) {
                return index;
            }
        }
        return -1; //Not found
    }

    public IdentityHashMap<E, Integer> getRemappingTable() {
        IdentityHashMap<E, Integer> remappingTable = new IdentityHashMap<>();
        final E[] components = this.components;
        final int positionCount = size;
        for(int i = 0; i != positionCount; ++i) {
            remappingTable.put(components[i], i);
        }
        return remappingTable;
    }
}
