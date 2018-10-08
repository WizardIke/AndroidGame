package wizardike.assignment3.graphics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Sprite {
    public float positionX, positionY, width, height;
    public float texU, texV, texWidth, texHeight;

    public Sprite() {}

    public Sprite(DataInputStream save) throws IOException {
        positionX = save.readFloat();
        positionY = save.readFloat();
        width = save.readFloat();
        height = save.readFloat();
        texU = save.readFloat();
        texV = save.readFloat();
        texWidth = save.readFloat();
        texHeight = save.readFloat();
    }

    void save(DataOutputStream save) throws IOException {
        save.writeFloat(positionX);
        save.writeFloat(positionY);
        save.writeFloat(width);
        save.writeFloat(height);
        save.writeFloat(texU);
        save.writeFloat(texV);
        save.writeFloat(texWidth);
        save.writeFloat(texHeight);
    }
}
