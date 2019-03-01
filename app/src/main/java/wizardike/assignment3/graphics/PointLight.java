package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector2;

public class PointLight {
    public Vector2 position;
    public float offsetX;
    public float offsetY;
    public float positionZ; //distance off ground
    public float radius;
    public float colorR;
    public float colorG;
    public float colorB;

    public PointLight(Vector2 position, float offsetX, float offsetY, float positionZ, float radius,
                      float colorR, float colorG, float colorB) {
        this.position = position;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.positionZ = positionZ;
        this.radius = radius;
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
    }

    public PointLight(DataInputStream save, Deserializer deserializer) throws IOException {
        position = deserializer.getObject(save.readInt());
        this.offsetX = save.readFloat();
        this.offsetY = save.readFloat();
        this.positionZ = save.readFloat();
        this.radius = save.readFloat();
        this.colorR = save.readFloat();
        this.colorG = save.readFloat();
        this.colorB = save.readFloat();
    }

    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeInt(serializer.getId(position));
        save.writeFloat(offsetX);
        save.writeFloat(offsetY);
        save.writeFloat(positionZ);
        save.writeFloat(radius);
        save.writeFloat(colorR);
        save.writeFloat(colorG);
        save.writeFloat(colorB);
    }
}
