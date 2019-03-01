package wizardike.assignment3;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityAllocator {
    private AtomicInteger nextEntity = new AtomicInteger(0);

    public int allocate() {
        return nextEntity.getAndIncrement();
    }
}
