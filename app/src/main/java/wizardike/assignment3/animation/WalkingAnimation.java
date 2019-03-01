package wizardike.assignment3.animation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;

/**
 * 0.0f directionX and 0.0f directionY means not moving
 */
public class WalkingAnimation {
    private WalkingSpriteSheet walkingSpriteSheet;
    private Sprite sprite;
    private float directionX;
    private float directionY;
    private float animationLength;
    private float animationTime;

    public WalkingAnimation(WalkingSpriteSheet walkingSpriteSheet, Sprite sprite,
                            float animationLength) {
        this.walkingSpriteSheet = walkingSpriteSheet;
        directionX = 0.0f;
        directionY = 0.0f;
        this.sprite = sprite;
        this.animationLength = animationLength;
        animationTime = 0.0f;
        sprite.texWidth = walkingSpriteSheet.spriteTextureWidth;
        sprite.texHeight = walkingSpriteSheet.spriteTextureHeight;
    }

    public WalkingAnimation(DataInputStream save, Deserializer deserializer) throws IOException {
        walkingSpriteSheet = deserializer.getObject(save.readInt());
        sprite = deserializer.getObject(save.readInt());
        directionX = save.readFloat();
        directionY = save.readFloat();
        animationLength = save.readFloat();
        animationTime = save.readFloat();
    }

    public void update(float frameTime) {
        animationTime += frameTime;
        if(animationTime >= animationLength) animationTime = 0.0f;

        int frameNum = 0;
        if(directionX != 0.0f || directionY != 0.0f) {
            frameNum = (int)(animationTime / animationLength * 9.0);
            if (directionX > 0.70711) {
                // Moving Right
                frameNum += 27;
            } else if (directionX < -0.70711) {
                // Moving Left
                frameNum += 9;
            }else if (directionY > 0.70711) {
                // Moving Down
                frameNum += 18;
            } //else Moving Up
        }

        float x = walkingSpriteSheet.xCoordinates[frameNum];
        float y = walkingSpriteSheet.yCoordinates[frameNum];
        sprite.texU = x;
        sprite.texV = y;
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeInt(serializer.getId(walkingSpriteSheet));
        save.writeInt(serializer.getId(sprite));
        save.writeFloat(directionX);
        save.writeFloat(directionY);
        save.writeFloat(animationLength);
        save.writeFloat(animationTime);
    }

    public void setDirectionX(float directionX) {
        this.directionX = directionX;
    }

    public void setDirectionY(float directionY) {
        this.directionY = directionY;
    }
}
