package wizardike.assignment3;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Savable {
    void save(DataOutputStream save) throws IOException;
}
