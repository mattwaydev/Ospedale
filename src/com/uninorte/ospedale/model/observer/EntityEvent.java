package com.uninorte.ospedale.model.observer;

/**
 * Payload mínimo de un evento de dominio.
 * La vista no recibe la entidad — sólo el tipo de cambio y el id,
 * y luego consulta datos frescos a través del controller.
 */
public final class EntityEvent {

    private final EventType type;
    private final String entityId;

    public EntityEvent(EventType type, String entityId) {
        this.type = type;
        this.entityId = entityId;
    }

    public EventType getType() { return type; }
    public String getEntityId() { return entityId; }
}
