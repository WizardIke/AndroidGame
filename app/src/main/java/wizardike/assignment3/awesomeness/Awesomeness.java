package wizardike.assignment3.awesomeness;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Awesomeness {
    private int value;

    public Awesomeness(int value) {
        this.value = value;
    }

    Awesomeness(DataInputStream save) throws IOException {
        value = save.readInt();
    }

    void save(DataOutputStream save) throws IOException {
        save.writeInt(value);
    }

    public void increase(int amount) {
        value += amount;
    }

    public int getValue() {
        return value;
    }
}
