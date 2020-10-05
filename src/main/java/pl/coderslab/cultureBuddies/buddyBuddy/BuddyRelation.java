package pl.coderslab.cultureBuddies.buddyBuddy;

import lombok.*;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "buddies_relations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "status", "added"})
public class BuddyRelation {
    @EmbeddedId
    private BuddyBuddyId id = new BuddyBuddyId();

    @ManyToOne
    @MapsId("buddyId")
    private Buddy buddy;
    @Transient
    private Long buddyId;

    @ManyToOne
    @MapsId("buddyOfId")
    private Buddy buddyOf;
    @Transient
    private Long buddyOfId;

    @ManyToOne
    private RelationStatus status;
    private LocalDateTime added;

    @PrePersist
    public void prePersist() {
        added = LocalDateTime.now();
    }
}
