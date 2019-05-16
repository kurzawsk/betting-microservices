package pl.kk.services.common.misc;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Long entityId, Class<?> class_) {
        super(String.format("%s with id %d was not found", class_.getSimpleName(), entityId));
    }

}
