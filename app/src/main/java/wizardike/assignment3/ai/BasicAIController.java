package wizardike.assignment3.ai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.movement.Movement;

/**
 * Constantly moves the entity towards the closest enemy.
 * Created by Isaac on 14/10/2017.
 */
public class BasicAIController {
    private float maxSpeed;

    public BasicAIController(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public BasicAIController(DataInputStream saveData) throws IOException {
        maxSpeed = saveData.readFloat();
    }

    public void update(Level level, int thisEntity) {
        Vector2 position = level.getPositionSystem().getPosition(thisEntity);
        Faction faction = level.getFactionSystem().getFaction(thisEntity);
        int[] otherCreature = level.getPositionSystem().getEntities();
        int otherCreaturesCount = level.getPositionSystem().entityCount();
        Targeting.Target target = Targeting.findClosestTarget(position, faction, otherCreature,
                otherCreaturesCount, level);
        Movement movement = level.getMovementSystem().getMovement(thisEntity);
        if(target != null){
            movement.directionX = target.directionX;
            movement.directionY = target.directionY;
            movement.currentSpeed = maxSpeed;
        } else {
            movement.currentSpeed = 0.0f;
        }
    }

    public void save(DataOutputStream saveData) throws IOException {
        saveData.writeFloat(maxSpeed);
    }
}