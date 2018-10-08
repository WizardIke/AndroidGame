package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class CircleShadowCaster {
    public float positionX, positionY;
    public float height;
    public float radius; //Must be positive
    public float ambientLightMultiplier;

    public CircleShadowCaster(float positionX, float positionY, float height, float radius,
                              float ambientLightMultiplier) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.height = height;
        this.radius = radius;
        this.ambientLightMultiplier = ambientLightMultiplier;
    }

    public CircleShadowCaster(DataInputStream save) throws IOException {
        positionX = save.readFloat();
        positionY = save.readFloat();
        height = save.readFloat();
        radius = save.readFloat();
        ambientLightMultiplier = save.readFloat();
    }

    void save(DataOutputStream save) throws IOException {
        save.writeFloat(positionX);
        save.writeFloat(positionY);
        save.writeFloat(height);
        save.writeFloat(radius);
        save.writeFloat(ambientLightMultiplier);
    }
}
