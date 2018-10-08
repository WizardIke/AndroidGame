package wizardike.assignment3.entities;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityAllocator {
    private AtomicInteger nextEntity = new AtomicInteger(Integer.MIN_VALUE);

    public int allocate() {
        return nextEntity.getAndIncrement();
    }
}
