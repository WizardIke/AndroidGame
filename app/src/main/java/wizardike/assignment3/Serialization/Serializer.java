package wizardike.assignment3.Serialization;

import java.util.HashMap;

public class Serializer {
    private int nextId = 0;
    private HashMap<Object, Integer> map = new HashMap<>();

    public void addObject(Object object) {
        map.put(object, nextId);
        ++nextId;
    }

    public int getId(Object object) {
        return map.get(object);
    }
}
