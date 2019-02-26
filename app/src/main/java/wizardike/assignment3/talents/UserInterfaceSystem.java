package wizardike.assignment3.talents;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.talents.Secondary.SecondaryTalent;
import wizardike.assignment3.talents.primary.PrimaryTalent;
import wizardike.assignment3.talents.primary.PrimaryTalentLoader;

public class UserInterfaceSystem {
    private ComponentStorage<PrimaryTalent> moveTalents;
    private ComponentStorage<PrimaryTalent> attackTalents;

    public static final int move = 0;
    public static final int attack = 1;
    public static final int secondary = 2;

    public UserInterfaceSystem() {
        moveTalents = new ComponentStorage<>(PrimaryTalent.class);
        attackTalents = new ComponentStorage<>(PrimaryTalent.class);
    }

    public UserInterfaceSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int moveCount = save.readInt();
        PrimaryTalent[] moves = new PrimaryTalent[moveCount];
        for(int i = 0; i != moveCount; ++i) {
            int id = save.readInt();
            moves[i] = PrimaryTalentLoader.load(id, save, spriteSheetRemappingTable);
        }
        int[] moveEntities = new int[moveCount];
        for(int i = 0; i != moveCount; ++i) {
            final int oldEntity = save.readInt();
            moveEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        moveTalents = new ComponentStorage<>(PrimaryTalent.class, moveEntities, moves);


        final int attackCount = save.readInt();
        PrimaryTalent[] attacks = new PrimaryTalent[moveCount];
        for(int i = 0; i != attackCount; ++i) {
            int id = save.readInt();
            attacks[i] = PrimaryTalentLoader.load(id, save, spriteSheetRemappingTable);
        }
        int[] attackEntities = new int[attackCount];
        for(int i = 0; i != attackCount; ++i) {
            final int oldEntity = save.readInt();
            attackEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        attackTalents = new ComponentStorage<>(PrimaryTalent.class, attackEntities, attacks);
    }

    public void addMoveTalent(int entity, PrimaryTalent talent) {
        moveTalents.addComponent(entity, talent);
    }

    public void removeMoveTalent(int entity, PrimaryTalent talent) {
        moveTalents.removeComponent(entity, talent);
    }

    public void removeMoveTalents(int entity) {
        moveTalents.removeComponents(entity);
    }

    public int indexOfMoveTalent(int entity, PrimaryTalent talent) {
        return moveTalents.indexOf(entity, talent);
    }

    public void addAttackTalent(int entity, PrimaryTalent talent) {
        attackTalents.addComponent(entity, talent);
    }

    public void removeAttackTalent(int entity, PrimaryTalent talent) {
        attackTalents.removeComponent(entity, talent);
    }

    public void removeAttackTalents(int entity) {
        attackTalents.removeComponents(entity);
    }

    public int indexOfAttackTalent(int entity, PrimaryTalent talent) {
        return attackTalents.indexOf(entity, talent);
    }

    public void update(Level level) {
        PrimaryTalent[] moves = moveTalents.getAllComponents();
        int moveCount = moveTalents.size();
        for(int i = 0; i != moveCount; ++i) {
            moves[i].update(level);
        }
        PrimaryTalent[] attacks = attackTalents.getAllComponents();
        int attackCount = attackTalents.size();
        for(int i = 0; i != attackCount; ++i) {
            attacks[i].update(level);
        }
        UserInterface userInterface = level.getEngine().getUserInterface();
        boolean leftStickDown;
        float leftDirectionX;
        float leftDirectionY;
        synchronized (userInterface) {
            leftStickDown = userInterface.leftStickDown;
            leftDirectionX = userInterface.leftDirectionX;
            leftDirectionY = userInterface.leftDirectionY;
        }
        if(leftStickDown) {
            int[] moveEntities = moveTalents.getAllEntities();
            for(int i = 0; i != moveCount; ++i) {
                moves[i].activate(level, moveEntities[i], leftDirectionX, leftDirectionY);
            }
        }
        boolean rightStickDown;
        float rightDirectionX;
        float rightDirectionY;
        synchronized (userInterface) {
            rightStickDown = userInterface.rightStickDown;
            rightDirectionX = userInterface.rightDirectionX;
            rightDirectionY = userInterface.rightDirectionY;
        }
        if(rightStickDown) {
            int[] attackEntities = attackTalents.getAllEntities();
            for(int i = 0; i != attackCount; ++i) {
                attacks[i].activate(level, attackEntities[i], rightDirectionX, rightDirectionY);
            }
        }
    }

    public void handleMessage(Level level, DataInputStream networkIn) throws IOException {
        int messageType = networkIn.readInt();
        int entityIndex = networkIn.readInt();
        switch (messageType) {
            case move: {
                moveTalents.getAllComponents()[entityIndex].handleMessage(level, networkIn, moveTalents.getAllEntities()[entityIndex]);
                break;
            }
            case attack: {
                attackTalents.getAllComponents()[entityIndex].handleMessage(level, networkIn, attackTalents.getAllEntities()[entityIndex]);
                break;
            }
        }
    }

    public void save(DataOutputStream save, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException {
        final PrimaryTalent[] moves = moveTalents.getAllComponents();
        final int moveCount = moveTalents.size();
        save.writeInt(moveCount);
        for(int i = 0; i != moveCount; ++i) {
            moves[i].save(save, spriteSheetRemappingTable);
        }

        final PrimaryTalent[] attacks = attackTalents.getAllComponents();
        final int attackCount = attackTalents.size();
        save.writeInt(attackCount);
        for(int i = 0; i != attackCount; ++i) {
            attacks[i].save(save, spriteSheetRemappingTable);
        }
    }

    public int indexOfSecondaryTalent(int entity, SecondaryTalent talent) {
        //TODO
        return -1;
    }
}
