package pl.coderslab.cultureBuddies.buddyBuddy;

import lombok.*;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import javax.persistence.*;

@Entity
@Table(name = "buddies_relations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
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


}
