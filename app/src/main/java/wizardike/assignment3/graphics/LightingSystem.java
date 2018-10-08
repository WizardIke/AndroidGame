package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entities.EntityAllocator;
import wizardike.assignment3.entities.EntityUpdater;
import wizardike.assignment3.geometry.IntersectionTesting;

public class LightingSystem {
    private final ComponentStorage<PointLight> pointLightComponentStorage;
    private final ComponentStorage<CircleShadowCaster> circleShadowCasterComponentStorage;
    private final ComponentStorage<LineShadowCaster> lineShadowCasterComponentStorage;

    public LightingSystem() {
        pointLightComponentStorage = new ComponentStorage<>();
        circleShadowCasterComponentStorage = new ComponentStorage<>();
        lineShadowCasterComponentStorage = new ComponentStorage<>();
    }

    public LightingSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int pointLightCount = save.readInt();
        final PointLight[] pointLights = new PointLight[pointLightCount];
        for(int i = 0; i != pointLightCount; ++i) {
            pointLights[i] = new PointLight(save);
        }
        int[] pointLightEntities = new int[pointLightCount];
        for(int i = 0; i != pointLightCount; ++i) {
            final int oldEntity = save.readInt();
            pointLightEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        pointLightComponentStorage = new ComponentStorage<>(pointLightEntities, pointLights);


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
        circleShadowCasterComponentStorage = new ComponentStorage<>(circleShadowCasterEntities, circleShadowCasters);


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
        lineShadowCasterComponentStorage = new ComponentStorage<>(lineShadowCasterEntities, lineShadowCasters);
    }

    public void update(Engine engine) {
        final GraphicsManager graphicsManager = engine.getGraphicsManager();
        final Camera camera = graphicsManager.getCamera();
        final GeometryBuffer geometryBuffer = graphicsManager.getGeometryBuffer();
        final LightBuffer lightBuffer = graphicsManager.getLightBuffer();
        final PointLight[] pointLights = pointLightComponentStorage.getAllComponents();
        final CircleShadowCaster[] circleShadowCasters = circleShadowCasterComponentStorage.getAllComponents();
        final LineShadowCaster[] lineShadowCasters = lineShadowCasterComponentStorage.getAllComponents();

        final float viewPortHalfWidth = 1 / camera.scaleX;
        final float viewPortHalfHeight = 1 / camera.scaleY;
        final int geometryColorTextureHandle = geometryBuffer.getColorTextureHandle();
        lightBuffer.prepareToRenderLights();
        for(PointLight light : pointLights) {
            if(IntersectionTesting.isIntersecting(light.positionX, light.positionY, light.radius,
                    camera.positionX, camera.positionY, viewPortHalfWidth, viewPortHalfHeight)) {
                lightBuffer.renderLight(light, camera);
                for(CircleShadowCaster shadowCaster : circleShadowCasters) {
                    lightBuffer.renderCircleShadow(shadowCaster, light);
                }
                for(LineShadowCaster shadowCaster : lineShadowCasters) {
                    lightBuffer.renderLineShadow(shadowCaster, light);
                }
                lightBuffer.applyLighting(geometryColorTextureHandle, light, camera);
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

    public void save(DataOutputStream save) throws IOException {
        final PointLight[] pointLights = pointLightComponentStorage.getAllComponents();
        save.writeInt(pointLights.length);
        for(PointLight pointLight : pointLights) {
            pointLight.save(save);
        }
        final int[] pointLightEntities = pointLightComponentStorage.getAllEntities();
        for (int entity : pointLightEntities) {
            save.writeInt(entity);
        }

        CircleShadowCaster[] circleShadowCasters = circleShadowCasterComponentStorage.getAllComponents();
        save.writeInt(circleShadowCasters.length);
        for(CircleShadowCaster shadowCaster : circleShadowCasters) {
            shadowCaster.save(save);
        }
        int[] circleShadowCasterEntities = circleShadowCasterComponentStorage.getAllEntities();
        for (int entity : circleShadowCasterEntities) {
            save.writeInt(entity);
        }

        LineShadowCaster[] lineShadowCasters = lineShadowCasterComponentStorage.getAllComponents();
        save.writeInt(lineShadowCasters.length);
        for(LineShadowCaster shadowCaster : lineShadowCasters) {
            shadowCaster.save(save);
        }
        int[] lineShadowCasterEntities = lineShadowCasterComponentStorage.getAllEntities();
        for (int entity : lineShadowCasterEntities) {
            save.writeInt(entity);
        }
    }
}
