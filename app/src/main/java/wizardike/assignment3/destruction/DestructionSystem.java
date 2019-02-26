package wizardike.assignment3.destruction;

import java.util.ArrayList;

import wizardike.assignment3.levels.Level;

public class DestructionSystem {
    private ArrayList<Integer> pendingEntities = new ArrayList<>();

    /**
     * Removes all components from an entity at the end of the frame
     * @param entity The entity to destroy
     */
    public void delayedDestroy(int entity) {
        pendingEntities.add(entity);
    }

    public void update(Level level) {
        for(int entity : pendingEntities) {
            level.destroyEntity(entity);
        }
        pendingEntities.clear();
    }
}
