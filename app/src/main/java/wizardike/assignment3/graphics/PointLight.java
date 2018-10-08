package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PointLight {
    public float positionX;
    public float positionY;
    public float positionZ; //distance off ground
    public float radius;
    public float colorR;
    public float colorG;
    public float colorB;

    public PointLight(float positionX, float positionY, float positionZ, float radius,
                      float colorR, float colorG, float colorB) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.radius = radius;
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
    }

    public PointLight(DataInputStream save) throws IOException {
        this.positionX = save.readFloat();
        this.positionY = save.readFloat();
        this.positionZ = save.readFloat();
        this.radius = save.readFloat();
        this.colorR = save.readFloat();
        this.colorG = save.readFloat();
        this.colorB = save.readFloat();
    }

    public void save(DataOutputStream save) throws IOException {
        save.writeFloat(positionX);
        save.writeFloat(positionY);
        save.writeFloat(positionZ);
        save.writeFloat(radius);
        save.writeFloat(colorR);
        save.writeFloat(colorG);
        save.writeFloat(colorB);
    }
}
