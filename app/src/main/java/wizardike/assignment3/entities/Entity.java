package wizardike.assignment3.entities;

public interface Entity {
    <T> T getComponent(Class<T> componentType);
}
