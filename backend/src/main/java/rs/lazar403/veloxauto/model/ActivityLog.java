package rs.lazar403.veloxauto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import rs.lazar403.veloxauto.enums.ActivityAction;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(
        name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_actor", columnList = "actor_id"),
                @Index(name = "idx_activity_action", columnList = "action"),
                @Index(name = "idx_activity_entity", columnList = "entity_type, entity_id"),
                @Index(name = "idx_activity_created", columnList = "created_at")
        }
)
public class ActivityLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [======== ACTOR ========]
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Customer actor; // nullable for system actions

    // [======== ACTION ========]
    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityAction action;

    // [======== TARGET ENTITY ========]
    @NotNull(message = "Entity type is required")
    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // e.g., "Vehicle", "Customer", "Reservation"

    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    // [======== DETAILS ========]
    @Size(max = 2000, message = "Details must not exceed 2000 characters")
    @Column(length = 2000)
    private String details; // JSON or plain text for additional context

    @Size(max = 45, message = "IP address must not exceed 45 characters")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Size(max = 500, message = "User agent must not exceed 500 characters")
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // [======== AUDIT ========]
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // [======== FACTORY METHODS ========]
    public static ActivityLog create(Customer actor, ActivityAction action, String entityType, Long entityId) {
        return ActivityLog.builder()
                .actor(actor)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .build();
    }

    public static ActivityLog create(Customer actor, ActivityAction action, String entityType, Long entityId, String details) {
        return ActivityLog.builder()
                .actor(actor)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
    }
}
