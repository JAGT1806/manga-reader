package com.jagt1806.mangareader.audit.listener;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jagt1806.mangareader.audit.auxiliary.AuditEvent;
import com.jagt1806.mangareader.audit.auxiliary.Auditable;
import com.jagt1806.mangareader.audit.model.Audit;
import com.jagt1806.mangareader.audit.repository.AuditRepository;
import com.jagt1806.mangareader.security.model.CustomUserDetails;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditEventListener {
  private final AuditRepository auditRepository;
  private final ObjectMapper objectMapper = new ObjectMapper()
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .registerModule(new JavaTimeModule());

  @Async
  @EventListener
  public void handleAuditEvent(AuditEvent event) {
    try {
      Audit audits = new Audit();
      audits.setAction(event.action());
      audits.setTableName(((Auditable) event.data()).getTableName());

      audits.setData(convertToJsonString(event.data()));
      audits.setTimestamp(LocalDateTime.now());

      // Obtener usuario actual
      CustomUserDetails userDetails = (CustomUserDetails) Optional
          .ofNullable(SecurityContextHolder.getContext().getAuthentication())
          .map(Authentication::getPrincipal)
          .filter(principal -> principal instanceof CustomUserDetails)
          .orElse(null);

      if (userDetails != null) {
        audits.setUserId(userDetails.getId());
        audits.setUserEmail(userDetails.getUsername());
      } else {
        audits.setUserId(null);
        audits.setUserEmail("system");
      }

      auditRepository.save(audits);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private String convertToJsonString(Object obj) {
    try {
      if (obj == null)
        return null;

      Map<String, Object> result = new HashMap<>();

      for (Field field : obj.getClass().getDeclaredFields()) {
        field.setAccessible(true);

        if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
          Object relatedEntity = field.get(obj);
          result.put(field.getName(), getEntityId(relatedEntity));
        } else if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
          Collection<?> relatedEntities = (Collection<?>) field.get(obj);
          result.put(field.getName(), relatedEntities.stream().map(this::getEntityId).toList());
        } else {
          result.put(field.getName(), field.get(obj));
        }
      }

      JsonNode jsonNode = objectMapper.valueToTree(result);

      return objectMapper.writeValueAsString(jsonNode);
    } catch (Exception e) {
      return null;
    }
  }

  private Object getEntityId(Object data) {
    if (data == null)
      return null;

    try {
      Field idField = data.getClass().getDeclaredField("id");
      idField.setAccessible(true);
      return idField.get(data);
    } catch (Exception e) {
      return null;
    }
  }
}
