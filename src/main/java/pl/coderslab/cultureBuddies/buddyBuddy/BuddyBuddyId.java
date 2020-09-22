package pl.coderslab.cultureBuddies.buddyBuddy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@ToString
public class BuddyBuddyId implements Serializable {
    @Column(name = "buddy_id")
    private Long buddyId;
    @Column(name = "buddy_of_id")
    private Long buddyOfId;

}
