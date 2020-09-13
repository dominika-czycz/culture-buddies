package pl.coderslab.cultureBuddies.buddies;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BuddyBookId implements Serializable {
    @Column(name = "buddy_id")
    private Long buddyId;
    @Column(name = "book_id")
    private Long bookId;

}

