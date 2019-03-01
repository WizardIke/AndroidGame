package wizardike.assignment3.ai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.animation.WalkingAnimation;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.velocity.Velocity;

public class BasicAIControllerHost {
    private final Velocity velocity;
    private boolean hadTarget;

    public BasicAIControllerHost(Vector2 position, float maxSpeed) {
        velocity = new Velocity(position);
        velocity.currentSpeed = maxSpeed;
        hadTarget = false;
    }

    BasicAIControllerHost(DataInputStream save, Deserializer deserializer) throws IOException {
        hadTarget = save.readBoolean();
        if(hadTarget) {
            velocity = deserializer.getObject(save.readInt());
        } else {
            velocity = new Velocity(save, deserializer);
        }
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeBoolean(hadTarget);
        if(hadTarget) {
            save.writeInt(serializer.getId(velocity));
        } else {
            velocity.save(save, serializer);
        }
    }

    public void update(Level level, int thisEntity) {
        Vector2 position = level.getPositionSystem().getPosition(thisEntity);
        Faction faction = level.getFactionSystem().getFaction(thisEntity);
        int[] otherCreature = level.getPositionSystem().getEntities();
        int otherCreaturesCount = level.getPositionSystem().positionsCount();
        Targeting.Target target = Targeting.findClosestTarget(position, faction, otherCreature,
                otherCreaturesCount, level);

        WalkingAnimation walkingAnimation = level.getWalkingAnimationSystem().getWalkingAnimation(thisEntity);
        if(target != null){
            velocity.directionX = target.directionX;
            velocity.directionY = target.directionY;
            if(!hadTarget) {
                level.getVelocityHostSystem().addVelocity(thisEntity, velocity);
            }

            if(walkingAnimation != null) {
                walkingAnimation.setDirectionX(target.directionX);
                walkingAnimation.setDirectionY(target.directionY);
            }
        } else {
            if(hadTarget) {
                level.getVelocityHostSystem().removeVelocity(thisEntity, velocity);
            }
            if(walkingAnimation != null) {
                walkingAnimation.setDirectionX(0.0f);
                walkingAnimation.setDirectionY(0.0f);
            }
        }
    }
}