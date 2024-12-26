package com.jagt1806.mangareader.audit.listener;

import com.jagt1806.mangareader.audit.auxiliary.AuditEvent;
import com.jagt1806.mangareader.audit.auxiliary.Auditable;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditListener {
    private final ApplicationEventPublisher eventPublisher;
    private static final ThreadLocal<Map<Object, Object>> ENTITY_STATE = ThreadLocal.withInitial(HashMap::new);

    @PostPersist
    public void postPersist(Object data) {
        if(data instanceof Auditable)
            publishAuditEvent(data, "INSERT");
    }

    @PostUpdate
    public void postUpdate(Object data) {
        if(data instanceof Auditable)
            publishAuditEvent(data, "UPDATE");
    }

    @PostRemove
    public void postRemove(Object data) {
        if(data instanceof Auditable)
            publishAuditEvent(data, "DELETE");
    }

    private void publishAuditEvent(Object data, String action) {
        if(data instanceof Auditable) {
            AuditEvent auditEvent = new AuditEvent(data, action);
            eventPublisher.publishEvent(auditEvent);
        }
    }

}
