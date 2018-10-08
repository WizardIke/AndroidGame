package wizardike.assignment3.updating;

import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.levels.Level;

public interface Updatable {
    void update(Level level);

    void save(DataOutputStream save) throws IOException;
    int getId();
}