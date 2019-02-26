package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.geometry.IntersectionTesting;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;

public class LightingSystem {
    private final ComponentStorage<PointLight> pointLightComponentStorage;
    private final ComponentStorage<CircleShadowCaster> circleShadowCasterComponentStorage;
    private final ComponentStorage<LineShadowCaster> lineShadowCasterComponentStorage;

    public LightingSystem() {
        pointLightComponentStorage = new ComponentStorage<>(PointLight.class);
        circleShadowCasterComponentStorage = new ComponentStorage<>(CircleShadowCaster.class);
        lineShadowCasterComponentStorage = new ComponentStorage<>(LineShadowCaster.class);
    }

    public LightingSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater,
                          Vector2[] positionRemappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int pointLightCount = save.readInt();
        final PointLight[] pointLights = new PointLight[pointLightCount];
        for(int i = 0; i != pointLightCount; ++i) {
            pointLights[i] = new PointLight(save, positionRemappingTable);
        }
        int[] pointLightEntities = new int[pointLightCount];
        for(int i = 0; i != pointLightCount; ++i) {
            final int oldEntity = save.readInt();
            pointLightEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        pointLightComponentStorage = new ComponentStorage<>(PointLight.class, pointLightEntities, pointLights);


        final int circleShadowCasterCount = save.readInt();
        final CircleShadowCaster[] circleShadowCasters = new CircleShadowCaster[circleShadowCasterCount];
        for(int i = 0; i != circleShadowCasterCount; ++i) {
            circleShadowCasters[i] = new CircleShadowCaster(save);
        }
        int[] circleShadowCasterEntities = new int[circleShadowCasterCount];
        for(int i = 0; i != circleShadowCasterCount; ++i) {
            final int oldEntity = save.readInt();
            circleShadowCasterEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        circleShadowCasterComponentStorage = new ComponentStorage<>(CircleShadowCaster.class, circleShadowCasterEntities, circleShadowCasters);


        final int lineShadowCasterCount = save.readInt();
        final LineShadowCaster[] lineShadowCasters = new LineShadowCaster[lineShadowCasterCount];
        for(int i = 0; i != lineShadowCasterCount; ++i) {
            lineShadowCasters[i] = new LineShadowCaster(save);
        }
        int[] lineShadowCasterEntities = new int[lineShadowCasterCount];
        for(int i = 0; i != lineShadowCasterCount; ++i) {
            final int oldEntity = save.readInt();
            lineShadowCasterEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        lineShadowCasterComponentStorage = new ComponentStorage<>(LineShadowCaster.class, lineShadowCasterEntities, lineShadowCasters);
    }

    public void update(Level level) {
        Engine engine = level.getEngine();
        final GraphicsManager graphicsManager = engine.getGraphicsManager();
        final Camera camera = level.getCamera();
        final GeometryBuffer geometryBuffer = graphicsManager.getGeometryBuffer();
        final LightBuffer lightBuffer = graphicsManager.getLightBuffer();
        final PointLight[] pointLights = pointLightComponentStorage.getAllComponents();
        final int pointLightCount = pointLightComponentStorage.size();
        final CircleShadowCaster[] circleShadowCasters = circleShadowCasterComponentStorage.getAllComponents();
        final int circleShadowCasterCount = circleShadowCasterComponentStorage.size();
        final LineShadowCaster[] lineShadowCasters = lineShadowCasterComponentStorage.getAllComponents();
        final int lineShadowCasterCount = lineShadowCasterComponentStorage.size();

        final float viewPortHalfWidth = 1 / (graphicsManager.getViewScaleX() * camera.zoom);
        final float viewPortHalfHeight = 1 / (graphicsManager.getViewScaleY() * camera.zoom);
        final int geometryColorTextureHandle = geometryBuffer.getColorTextureHandle();
        lightBuffer.prepareToRenderLights();
        for(int i = 0; i != pointLightCount; ++i) {
            PointLight light = pointLights[i];
            if(IntersectionTesting.isIntersecting(light.position.getX() + light.offsetX,
                    light.position.getY() + light.offsetY, light.radius,
                    camera.position.getX(), camera.position.getY(), viewPortHalfWidth, viewPortHalfHeight)) {
                lightBuffer.renderLight(light, camera, graphicsManager);
                for(int j = 0; j != circleShadowCasterCount; ++j) {
                    lightBuffer.renderCircleShadow(circleShadowCasters[j], light);
                }
                for(int j = 0; j != lineShadowCasterCount; ++j) {
                    lightBuffer.renderLineShadow(lineShadowCasters[j], light);
                }
                lightBuffer.applyLighting(geometryColorTextureHandle, light, camera, graphicsManager);
            }
        }
    }

    public void addPointLight(int entity, PointLight light) {
        pointLightComponentStorage.addComponent(entity, light);
    }

    public void removePointLights(int entity) {
        pointLightComponentStorage.removeComponents(entity);
    }

    public void addCircleShadowCaster(int entity, CircleShadowCaster circleShadowCaster) {
        circleShadowCasterComponentStorage.addComponent(entity, circleShadowCaster);
    }

    public void removeCircleShadowCasters(int entity) {
        circleShadowCasterComponentStorage.removeComponents(entity);
    }

    public void addLineShadowCaster(int entity, LineShadowCaster lineShadowCaster) {
        lineShadowCasterComponentStorage.addComponent(entity, lineShadowCaster);
    }

    public void removeLineShadowCasters(int entity) {
        lineShadowCasterComponentStorage.removeComponents(entity);
    }

    public void save(DataOutputStream save, IdentityHashMap<Vector2, Integer> positionRemappingTable) throws IOException {
        final PointLight[] pointLights = pointLightComponentStorage.getAllComponents();
        final int pointLightCount = pointLightComponentStorage.size();
        save.writeInt(pointLightCount);
        for(int i = 0; i != pointLightCount; ++i) {
            pointLights[i].save(save, positionRemappingTable);
        }
        final int[] pointLightEntities = pointLightComponentStorage.getAllEntities();
        for (int i = 0; i != pointLightCount; ++i) {
            save.writeInt(pointLightEntities[i]);
        }

        CircleShadowCaster[] circleShadowCasters = circleShadowCasterComponentStorage.getAllComponents();
        final int circleShadowCasterCount = circleShadowCasterComponentStorage.size();
        save.writeInt(circleShadowCasterCount);
        for(int i = 0; i != circleShadowCasterCount; ++i) {
            circleShadowCasters[i].save(save);
        }
        int[] circleShadowCasterEntities = circleShadowCasterComponentStorage.getAllEntities();
        for (int i = 0; i != circleShadowCasterCount; ++i) {
            save.writeInt(circleShadowCasterEntities[i]);
        }

        LineShadowCaster[] lineShadowCasters = lineShadowCasterComponentStorage.getAllComponents();
        final int lineShadowCasterCount = lineShadowCasterComponentStorage.size();
        save.writeInt(lineShadowCasterCount);
        for(int i = 0; i != lineShadowCasterCount; ++i) {
            lineShadowCasters[i].save(save);
        }
        int[] lineShadowCasterEntities = lineShadowCasterComponentStorage.getAllEntities();
        for (int i = 0; i != lineShadowCasterCount; ++i) {
            save.writeInt(lineShadowCasterEntities[i]);
        }
    }
}
