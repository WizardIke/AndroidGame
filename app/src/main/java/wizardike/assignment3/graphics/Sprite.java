package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.geometry.Vector2;

public class Sprite {
    public Vector2 position;
    public float offsetX, offsetY, width, height;
    public float texU, texV, texWidth, texHeight;

    public Sprite() {}

    public Sprite(Vector2 position, float offsetX, float offsetY, float width, float height,
                  float texU, float texV, float texWidth, float texHeight) {
        this.position = position;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.texU = texU;
        this.texV = texV;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public Sprite(DataInputStream save, Deserializer deserializer) throws IOException {
        position = deserializer.getObject(save.readInt());
        offsetX = save.readFloat();
        offsetY = save.readFloat();
        width = save.readFloat();
        height = save.readFloat();
        texU = save.readFloat();
        texV = save.readFloat();
        texWidth = save.readFloat();
        texHeight = save.readFloat();
    }

    void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeInt(serializer.getId(position));
        save.writeFloat(offsetX);
        save.writeFloat(offsetY);
        save.writeFloat(width);
        save.writeFloat(height);
        save.writeFloat(texU);
        save.writeFloat(texV);
        save.writeFloat(texWidth);
        save.writeFloat(texHeight);
    }
}
