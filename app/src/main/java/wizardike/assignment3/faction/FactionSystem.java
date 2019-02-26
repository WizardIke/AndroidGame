package wizardike.assignment3.faction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;

public class FactionSystem {
    private final ComponentStorage<Faction> factionComponentStorage;

    public FactionSystem() {
        factionComponentStorage = new ComponentStorage<>(Faction.class);
    }

    public FactionSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int factionCount = save.readInt();
        Faction[] factions = new Faction[factionCount];
        for(int i = 0; i != factionCount; ++i) {
            try {
                factions[i] = Faction.toFaction(save.readInt());
            } catch (InvalidFactionException e) {
                e.printStackTrace();
            }
        }
        int[] factionEntities = new int[factionCount];
        for(int i = 0; i != factionCount; ++i) {
            final int oldEntity = save.readInt();
            factionEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        factionComponentStorage = new ComponentStorage<>(Faction.class, factionEntities, factions);
    }

    public Faction getFaction(int entity) {
        return factionComponentStorage.getComponent(entity);
    }

    public void addFaction(int entity, Faction faction) {
        factionComponentStorage.addComponent(entity, faction);
    }

    public void removeFactions(int entity) {
        factionComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save) throws IOException {
        final Faction[] factions = factionComponentStorage.getAllComponents();
        final int factionCount = factionComponentStorage.size();
        save.writeInt(factionCount);
        for(int i = 0; i != factionCount; ++i) {
            save.writeInt(factions[i].value);
        }

        int[] entities = factionComponentStorage.getAllEntities();
        for (int i = 0; i != factionCount; ++i) {
            save.writeInt(entities[i]);
        }
    }
}
