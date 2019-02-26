package wizardike.assignment3.graphics;

import android.os.Build;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;

public class GeometrySystem {
    private final ComponentStorage<Sprite> spriteComponentStorage;
    private final ComponentStorage<Sprite> transparentSpriteComponentStorage;

    public GeometrySystem() {
        spriteComponentStorage = new ComponentStorage<>(Sprite.class);
        transparentSpriteComponentStorage = new ComponentStorage<>(Sprite.class);
    }

    public GeometrySystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater,
                          Vector2[] remappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int spriteCount = save.readInt();
        Sprite[] sprites = new Sprite[spriteCount];
        for(int i = 0; i != spriteCount; ++i) {
            sprites[i] = new Sprite(save, remappingTable);
        }
        int[] entities = new int[spriteCount];
        for(int i = 0; i != spriteCount; ++i) {
            final int oldEntity = save.readInt();
            entities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        spriteComponentStorage = new ComponentStorage<>(Sprite.class, entities, sprites);

        final int transparentSpriteCount = save.readInt();
        Sprite[] transparentSprites = new Sprite[transparentSpriteCount];
        for(int i = 0; i != transparentSpriteCount; ++i) {
            transparentSprites[i] = new Sprite(save, remappingTable);
        }
        int[] transparentEntities = new int[transparentSpriteCount];
        for(int i = 0; i != transparentSpriteCount; ++i) {
            final int oldEntity = save.readInt();
            transparentEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        transparentSpriteComponentStorage = new ComponentStorage<>(Sprite.class, transparentEntities, transparentSprites);
    }

    public void update(Level level) {
        Engine engine = level.getEngine();
        final GraphicsManager graphicsManager = engine.getGraphicsManager();
        final GeometryBuffer geometryBuffer = graphicsManager.getGeometryBuffer();
        final TextureManager textureManager = graphicsManager.getTextureManager();
        final Camera camera = level.getCamera();
        final int currentBufferIndex = graphicsManager.getCurrentBufferIndex();
        final Sprite[] sprites = spriteComponentStorage.getAllComponents();
        final Sprite[] transparentSprites = transparentSpriteComponentStorage.getAllComponents();

        if(graphicsManager.getOpenGlVersion() >= 30 && Build.VERSION.SDK_INT >= 18) {
            geometryBuffer.prepareToGenerateMeshes30(currentBufferIndex);

            //create geometry and add it to the buffer
            int spritesOffset = geometryBuffer.getCurrentMeshOffset();
            int numberOfSprites = spriteComponentStorage.size();
            for(int i = 0; i != numberOfSprites; ++i) {
                geometryBuffer.drawSprite30(sprites[i]);
            }
            int transparentSpritesOffset = geometryBuffer.getCurrentMeshOffset();
            int transparentNumberOfSprites = transparentSpriteComponentStorage.size();
            for(int i = 0; i != transparentNumberOfSprites; ++i) {
                geometryBuffer.drawSprite30(transparentSprites[i]);
            }
            geometryBuffer.finishGeneratingMeshes30();

            geometryBuffer.prepareToRenderMeshes(textureManager.getTextureHandle());
            if(graphicsManager.isDepthTextureSupported()) {
                geometryBuffer.prepareToRenderOpaqueSpritesUsingDepthTexture30(camera, graphicsManager);
                geometryBuffer.renderOpaqueSpritesMesh30(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesUsingDepthTexture30();
            } else {
                geometryBuffer.prepareToRenderOpaqueSpritesDepthOnly30(camera, graphicsManager);
                geometryBuffer.renderOpaqueSpritesMesh30(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesDepthOnly30();

                geometryBuffer.prepareToRenderOpaqueSpritesColorOnly30(camera, graphicsManager);
                geometryBuffer.renderOpaqueSpritesMesh30(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesColorOnly30();
            }

            geometryBuffer.prepareToRenderTransparentSprites30(camera, graphicsManager);
            geometryBuffer.renderTransparentSpritesMesh30(transparentSpritesOffset, transparentNumberOfSprites);
            geometryBuffer.finishRenderingTransparentSprites30();
        } else {
            //create geometry and add it to the buffer
            int spritesOffset = geometryBuffer.getCurrentMeshOffset();
            int numberOfSprites = spriteComponentStorage.size();
            for(int i = 0; i != numberOfSprites; ++i) {
                geometryBuffer.drawSprite20(sprites[i]);
            }
            int transparentSpritesOffset = geometryBuffer.getCurrentMeshOffset();
            int transparentNumberOfSprites = transparentSpriteComponentStorage.size();
            for(int i = 0; i != transparentNumberOfSprites; ++i) {
                geometryBuffer.drawSprite20(transparentSprites[i]);
            }

            geometryBuffer.prepareToRenderMeshes(textureManager.getTextureHandle());
            if(graphicsManager.isDepthTextureSupported()) {
                geometryBuffer.prepareToRenderOpaqueSpritesUsingDepthTexture20(camera, graphicsManager);
                geometryBuffer.renderOpaqueSpritesMesh20(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesUsingDepthTexture20();
            } else {
                geometryBuffer.prepareToRenderOpaqueSpritesDepthOnly20(camera, graphicsManager);
                geometryBuffer.renderOpaqueSpritesMesh20(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesDepthOnly20();

                geometryBuffer.prepareToRenderOpaqueSpritesColorOnly20(camera, graphicsManager);
                geometryBuffer.renderOpaqueSpritesMesh20(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesColorOnly20();
            }

            geometryBuffer.prepareToRenderTransparentSprites20(camera, graphicsManager);
            geometryBuffer.renderTransparentSpritesMesh20(transparentSpritesOffset, transparentNumberOfSprites);
            geometryBuffer.finishRenderingTransparentSprites20();
        }
    }

    public void addSprite(int entity, Sprite sprite) {
        spriteComponentStorage.addComponent(entity, sprite);
    }

    public void removeSprites(int entity) {
        spriteComponentStorage.removeComponents(entity);
    }

    public void addTransparentSprite(int entity, Sprite sprite) {
        transparentSpriteComponentStorage.addComponent(entity, sprite);

        //keep the transparent sprites sorted
        final Sprite[] sprites = transparentSpriteComponentStorage.getAllComponents();
        int index = transparentSpriteComponentStorage.size() - 1;
        while(index != 0) {
            final int newIndex = index - 1;
            if((sprites[newIndex].position.getY() + sprites[newIndex].offsetY + sprites[newIndex].height)
                    > (sprites[index].position.getY() + sprites[index].offsetY + sprites[index].height)) {
                transparentSpriteComponentStorage.swapComponents(newIndex, index);
            } else {
                break;
            }
            index = newIndex;
        }
    }

    public void removeTransparentSprites(int entity) {
        transparentSpriteComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, IdentityHashMap<Vector2, Integer> remappingTable) throws IOException {
        Sprite[] sprites = spriteComponentStorage.getAllComponents();
        final int spriteCount = spriteComponentStorage.size();
        save.writeInt(spriteCount);
        for(int i = 0; i != spriteCount; ++i) {
            sprites[i].save(save, remappingTable);
        }
        int[] entities = spriteComponentStorage.getAllEntities();
        for (int i = 0; i != spriteCount; ++i) {
            save.writeInt(entities[i]);
        }

        Sprite[] transparentSprites = transparentSpriteComponentStorage.getAllComponents();
        final int transparentSpriteCount = spriteComponentStorage.size();
        save.writeInt(transparentSpriteCount);
        for(int i = 0; i != transparentSpriteCount; ++i) {
            transparentSprites[i].save(save, remappingTable);
        }
        int[] transparentEntities = transparentSpriteComponentStorage.getAllEntities();
        for (int i = 0; i != transparentSpriteCount; ++i) {
            save.writeInt(transparentEntities[spriteCount]);
        }
    }

    /**
     * Note the array might be longer than the number of sprites
     */
    public Sprite[] getSprites() {
        return spriteComponentStorage.getAllComponents();
    }

    public int getSpriteCount() {
        return spriteComponentStorage.size();
    }

    /**
     * Note the array might be longer than the number of transparent sprites
     */
    public Sprite[] getTransparentSprites() {
        return transparentSpriteComponentStorage.getAllComponents();
    }

    public int getTransparentSpriteCount() {
        return transparentSpriteComponentStorage.size();
    }

    public IdentityHashMap<Sprite, Integer> getSpriteRemappingTable() {
        return spriteComponentStorage.getRemappingTable();
    }

    public IdentityHashMap<Sprite, Integer> getTransparentSpriteRemappingTable() {
        return transparentSpriteComponentStorage.getRemappingTable();
    }
}
