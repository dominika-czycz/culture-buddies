package pl.coderslab.cultureBuddies.buddies;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BuddyBookId implements Serializable {
    @Column(name = "buddy_id")
    private Long buddyId;
    @Column(name = "book_id")
    private Long bookId;

    public BuddyBookId(Long buddyId, Long bookId) {
        this.buddyId = buddyId;
        this.bookId = bookId;
    }

    public BuddyBookId() {
    }

    public Long getBuddyId() {
        return this.buddyId;
    }

    public Long getBookId() {
        return this.bookId;
    }

    public void setBuddyId(Long buddyId) {
        this.buddyId = buddyId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}

