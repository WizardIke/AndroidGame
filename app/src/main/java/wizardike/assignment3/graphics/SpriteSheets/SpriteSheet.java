package wizardike.assignment3.graphics.SpriteSheets;

import java.io.DataOutputStream;
import java.io.IOException;

public interface SpriteSheet {
    void save(DataOutputStream save) throws IOException;
    int getId();
}
