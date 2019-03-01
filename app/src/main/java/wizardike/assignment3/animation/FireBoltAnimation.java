package wizardike.assignment3.animation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;

public class FireBoltAnimation {
    private static final float explodingGrowTime = 0.05f;
    private static final float explodingShrinkTime = 0.15f;
    private static final float travelAnimationLength = 0.1f;

    private static final int travelling = 0;
    private static final int growing = 1;
    private static final int shrinking = 2;

    private WalkingSpriteSheet spriteSheet;
    private Sprite sprite;
    private float animationTime = 0.0f;
    private int state = travelling;
    private int offsetInCoordinates;

    public FireBoltAnimation(WalkingSpriteSheet spriteSheet, int offsetInCoordinates, Sprite sprite) {
        this.spriteSheet = spriteSheet;
        this.offsetInCoordinates = offsetInCoordinates;
        this.sprite = sprite;
        sprite.texWidth = spriteSheet.spriteTextureWidth;
        sprite.texHeight = spriteSheet.spriteTextureHeight;
    }

    public FireBoltAnimation(DataInputStream save, Deserializer deserializer) throws IOException {
        spriteSheet = deserializer.getObject(save.readInt());
        sprite = deserializer.getObject(save.readInt());
        animationTime = save.readFloat();
        state = save.readInt();
        offsetInCoordinates = save.readInt();
    }

    public void explode() {
        animationTime = 0.0f;
        state = growing;
    }

    public void update(float frameTime) {
        if(state == travelling) {
            animationTime += frameTime;
            if(animationTime >= travelAnimationLength) animationTime = 0.0f;
            int frameNum = (int)(animationTime / travelAnimationLength * 2.0f) + offsetInCoordinates + 1;
            sprite.texU = spriteSheet.xCoordinates[frameNum];
            sprite.texV = spriteSheet.yCoordinates[frameNum];
        } else if(state == growing) {
            float oldAnimationTime = animationTime;
            animationTime += frameTime;
            float timeDelta = animationTime;
            if(animationTime >= explodingGrowTime) {
                timeDelta = explodingGrowTime;
                animationTime = 0.0f;
            }
            float multiplier = (timeDelta + explodingGrowTime) / (oldAnimationTime + explodingGrowTime);
            sprite.width *= multiplier;
            sprite.height *= multiplier;
            sprite.offsetX = -sprite.width / 2.0f;
            sprite.offsetY = -sprite.height / 2.0f;
        } else if(state == shrinking) {
            animationTime += frameTime;
            if(animationTime >= explodingShrinkTime) {
                animationTime = 0.95f * explodingShrinkTime;
            }
            int frameNum = (int)(animationTime / explodingShrinkTime * 16.0f) + offsetInCoordinates;
            sprite.texU = spriteSheet.xCoordinates[frameNum];
            sprite.texV = spriteSheet.yCoordinates[frameNum];
        }
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeInt(serializer.getId(spriteSheet));
        save.writeInt(serializer.getId(sprite));
        save.writeFloat(animationTime);
        save.writeInt(state);
        save.writeInt(offsetInCoordinates);
    }
}
