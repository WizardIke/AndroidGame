package wizardike.assignment3.graphics;

import android.os.Build;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entities.EntityAllocator;
import wizardike.assignment3.entities.EntityUpdater;

public class GeometrySystem {
    private final ComponentStorage<Sprite> spriteComponentStorage;
    private final ComponentStorage<Sprite> transparentSpriteComponentStorage;

    public GeometrySystem() {
        spriteComponentStorage = new ComponentStorage<>();
        transparentSpriteComponentStorage = new ComponentStorage<>();
    }

    public GeometrySystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int spriteCount = save.readInt();
        Sprite[] sprites = new Sprite[spriteCount];
        for(int i = 0; i != spriteCount; ++i) {
            sprites[i] = new Sprite(save);
        }
        int[] entities = new int[spriteCount];
        for(int i = 0; i != spriteCount; ++i) {
            final int oldEntity = save.readInt();
            entities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        spriteComponentStorage = new ComponentStorage<>(entities, sprites);

        final int transparentSpriteCount = save.readInt();
        Sprite[] transparentSprites = new Sprite[transparentSpriteCount];
        for(int i = 0; i != transparentSpriteCount; ++i) {
            transparentSprites[i] = new Sprite(save);
        }
        int[] transparentEntities = new int[transparentSpriteCount];
        for(int i = 0; i != transparentSpriteCount; ++i) {
            final int oldEntity = save.readInt();
            entities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        transparentSpriteComponentStorage = new ComponentStorage<>(transparentEntities, transparentSprites);
    }

    public void update(Engine engine) {
        final GraphicsManager graphicsManager = engine.getGraphicsManager();
        final GeometryBuffer geometryBuffer = graphicsManager.getGeometryBuffer();
        final TextureManager textureManager = graphicsManager.getTextureManager();
        final Camera camera = graphicsManager.getCamera();
        final int currentBufferIndex = graphicsManager.getCurrentBufferIndex();
        final Sprite[] sprites = spriteComponentStorage.getAllComponents();
        final Sprite[] transparentSprites = transparentSpriteComponentStorage.getAllComponents();

        if(graphicsManager.getOpenGlVersion() >= 30 && Build.VERSION.SDK_INT >= 18) {
            geometryBuffer.prepareToGenerateMeshes30(currentBufferIndex);

            //create geometry and add it to the buffer
            int spritesOffset = geometryBuffer.getCurrentMeshOffset();
            int numberOfSprites = sprites.length;
            for(Sprite sprite : sprites) {
                geometryBuffer.drawSprite30(sprite);
            }
            int transparentSpritesOffset = geometryBuffer.getCurrentMeshOffset();
            int transparentNumberOfSprites = transparentSprites.length;
            for(Sprite transparentSprite : transparentSprites) {
                geometryBuffer.drawSprite30(transparentSprite);
            }
            geometryBuffer.finishGeneratingMeshes30();

            geometryBuffer.prepareToRenderMeshes(textureManager.getTextureHandle());
            if(graphicsManager.isDepthTextureSupported()) {
                geometryBuffer.prepareToRenderOpaqueSpritesUsingDepthTexture30(camera);
                geometryBuffer.renderOpaqueSpritesMesh30(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesUsingDepthTexture30();
            } else {
                geometryBuffer.prepareToRenderOpaqueSpritesDepthOnly30(camera);
                geometryBuffer.renderOpaqueSpritesMesh30(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesDepthOnly30();

                geometryBuffer.prepareToRenderOpaqueSpritesColorOnly30(camera);
                geometryBuffer.renderOpaqueSpritesMesh30(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesColorOnly30();
            }

            geometryBuffer.prepareToRenderTransparentSprites30(camera);
            geometryBuffer.renderTransparentSpritesMesh30(transparentSpritesOffset, transparentNumberOfSprites);
            geometryBuffer.finishRenderingTransparentSprites30();
        } else {
            //create geometry and add it to the buffer
            int spritesOffset = geometryBuffer.getCurrentMeshOffset();
            int numberOfSprites = sprites.length;
            for(Sprite sprite : sprites) {
                geometryBuffer.drawSprite20(sprite);
            }
            int transparentSpritesOffset = geometryBuffer.getCurrentMeshOffset();
            int transparentNumberOfSprites = transparentSprites.length;
            for(Sprite sprite : transparentSprites) {
                geometryBuffer.drawSprite20(sprite);
            }

            geometryBuffer.prepareToRenderMeshes(textureManager.getTextureHandle());
            if(graphicsManager.isDepthTextureSupported()) {
                geometryBuffer.prepareToRenderOpaqueSpritesUsingDepthTexture20(camera);
                geometryBuffer.renderOpaqueSpritesMesh20(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesUsingDepthTexture20();
            } else {
                geometryBuffer.prepareToRenderOpaqueSpritesDepthOnly20(camera);
                geometryBuffer.renderOpaqueSpritesMesh20(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesDepthOnly20();

                geometryBuffer.prepareToRenderOpaqueSpritesColorOnly20(camera);
                geometryBuffer.renderOpaqueSpritesMesh20(spritesOffset, numberOfSprites);
                geometryBuffer.finishRenderingOpaqueSpritesColorOnly20();
            }

            geometryBuffer.prepareToRenderTransparentSprites20(camera);
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
        int index = sprites.length - 1;
        while(index != 0) {
            final int newIndex = index - 1;
            if((sprites[newIndex].positionY + sprites[newIndex].height) > (sprites[index].positionY + sprites[index].height)) {
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

    public void save(DataOutputStream save) throws IOException {
        Sprite[] sprites = spriteComponentStorage.getAllComponents();
        save.writeInt(sprites.length);
        for(Sprite sprite : sprites) {
            sprite.save(save);
        }
        int[] entities = spriteComponentStorage.getAllEntities();
        for (int entity : entities) {
            save.writeInt(entity);
        }

        Sprite[] transparentSprites = transparentSpriteComponentStorage.getAllComponents();
        save.writeInt(transparentSprites.length);
        for(Sprite sprite : transparentSprites) {
            sprite.save(save);
        }
        int[] transparentEntities = transparentSpriteComponentStorage.getAllEntities();
        for (int entity : transparentEntities) {
            save.writeInt(entity);
        }
    }
}
