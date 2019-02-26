package wizardike.assignment3.assemblies;

import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.TreeSpriteSheet;
import wizardike.assignment3.levels.Level;

/**
 * Created by Isaac on 2/02/2017.
 */
public class Tree2 {
    private static final float width = 0.50322582f;
    private static final float height = 0.75666666f;

    public static int create(Level level, float posX, float posY, TreeSpriteSheet spriteSheet) {
        Vector2 position = new Vector2(posX, posY);
        int entity = Tree.create(level, position, 0.15f * width);
        level.getPositionSystem().addPosition(entity, position);
        Sprite sprite = new Sprite();
        sprite.position = position;
        sprite.offsetX = -0.5f * width;
        sprite.offsetY = -0.95f * height;
        sprite.width = width;
        sprite.height = height;
        sprite.texU = spriteSheet.xCoordinates[1];
        sprite.texV = spriteSheet.yCoordinates[1];
        sprite.texWidth = spriteSheet.spriteTextureWidths[1];
        sprite.texHeight = spriteSheet.spriteTextureHeights[1];
        level.getGeometrySystem().addSprite(entity, sprite);
        return entity;
    }
}
