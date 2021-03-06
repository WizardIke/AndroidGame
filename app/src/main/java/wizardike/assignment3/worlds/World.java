package wizardike.assignment3.worlds;

import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;

public interface World {
    void start();
    void update();
    void stop();
    void save(DataOutputStream save) throws IOException;
    int getId();
}
