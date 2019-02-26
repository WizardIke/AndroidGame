package wizardike.assignment3.animation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.physics.movement.Movement;

public class WalkingAnimation {
    private WalkingSpriteSheet walkingSpriteSheet;
    private Movement movement;
    private Sprite sprite;
    private float animationLength;
    private float animationTime;

    public WalkingAnimation(WalkingSpriteSheet walkingSpriteSheet, Movement movement, Sprite sprite,
                            float animationLength) {
        this.walkingSpriteSheet = walkingSpriteSheet;
        this.movement = movement;
        this.sprite = sprite;
        this.animationLength = animationLength;
        animationTime = 0.0f;
        sprite.texWidth = walkingSpriteSheet.spriteTextureWidth;
        sprite.texHeight = walkingSpriteSheet.spriteTextureHeight;
    }

    public WalkingAnimation(DataInputStream save,
                            SpriteSheet[] spriteSheetRemappingTable,
                            Movement[] movementRemappingTable,
                            Sprite[] spriteRemappingTable) throws IOException {
        walkingSpriteSheet = (WalkingSpriteSheet)spriteSheetRemappingTable[save.readInt()];
        movement = movementRemappingTable[save.readInt()];
        sprite = spriteRemappingTable[save.readInt()];
        animationLength = save.readFloat();
        animationTime = save.readFloat();
    }

    public void update(float frameTime) {
        animationTime += frameTime;
        if(animationTime >= animationLength) animationTime = 0.0f;

        int frameNum = 0;
        if(movement.currentSpeed != 0.0f) {
            frameNum = (int)(animationTime / animationLength * 9.0);
            if (movement.directionX > 0.70711) {
                // Moving Right
                frameNum += 27;
            } else if (movement.directionX < -0.70711) {
                // Moving Left
                frameNum += 9;
            }else if (movement.directionY > 0.70711) {
                // Moving Down
                frameNum += 18;
            } //else Moving Up
        }

        float x = walkingSpriteSheet.xCoordinates[frameNum];
        float y = walkingSpriteSheet.yCoordinates[frameNum];
        sprite.texU = x;
        sprite.texV = y;
    }

    public void save(DataOutputStream save, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable,
                     IdentityHashMap<Movement, Integer> movementRemappingTable,
                     IdentityHashMap<Sprite, Integer> spriteRemappingTable) throws IOException {
        save.writeInt(spriteSheetRemappingTable.get(walkingSpriteSheet));
        save.writeInt(movementRemappingTable.get(movement));
        save.writeInt(spriteRemappingTable.get(sprite));
        save.writeFloat(animationLength);
        save.writeFloat(animationTime);
    }
}
