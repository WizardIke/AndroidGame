package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LineShadowCaster {
    public float startX, startY; //Must be ordered with end correctly for back face culling
    public float endX, endY;
    public float height;
    public float ambientLightMultiplier;

    public LineShadowCaster(float startX, float startY, float endX, float endY, float height,
                            float ambientLightMultiplier) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.height = height;
        this.ambientLightMultiplier = ambientLightMultiplier;
    }

    public LineShadowCaster(DataInputStream save) throws IOException {
        startX = save.readFloat();
        startY = save.readFloat();
        endX = save.readFloat();
        endY = save.readFloat();
        height = save.readFloat();
        ambientLightMultiplier = save.readFloat();
    }

    void save(DataOutputStream save) throws IOException {
        save.writeFloat(startX);
        save.writeFloat(startY);
        save.writeFloat(endX);
        save.writeFloat(endY);
        save.writeFloat(height);
        save.writeFloat(ambientLightMultiplier);
    }
}
