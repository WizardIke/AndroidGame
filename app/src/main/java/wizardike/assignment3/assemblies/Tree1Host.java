package wizardike.assignment3.assemblies;

import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.TreeSpriteSheet;
import wizardike.assignment3.levels.Level;

public class Tree1Host {
    private static final float width = 0.7548f;
    private static final float height = 1.0086f;

    public static int create(Level level, float posX, float posY, TreeSpriteSheet spriteSheet) {
        Vector2 position = new Vector2(posX, posY);
        int entity = Tree.create(level, position, width * 0.16f);
        level.getPositionHostSystem().addPosition(entity, position);
        Sprite sprite = new Sprite();
        sprite.position = position;
        sprite.offsetX = -0.5f * width;
        sprite.offsetY = -0.8f * height;
        sprite.width = width;
        sprite.height = height;
        sprite.texU = spriteSheet.xCoordinates[0];
        sprite.texV = spriteSheet.yCoordinates[0];
        sprite.texWidth = spriteSheet.spriteTextureWidths[0];
        sprite.texHeight = spriteSheet.spriteTextureHeights[0];
        level.getGeometrySystem().addSprite(entity, sprite);
        return entity;
    }
}
